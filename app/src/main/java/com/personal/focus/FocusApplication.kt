package com.personal.focus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.personal.focus.data.FocusDatabase
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.data.repository.AppBlockRepository
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.data.repository.FocusTimerRepository
import com.personal.focus.data.repository.UsageStatsRepository
import com.personal.focus.data.repository.YouTubeStudyRepository
import com.personal.focus.ui.launcher.ProxyLauncherActivity
import com.personal.focus.ui.overlay.AppBlockOverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FocusApplication : Application() {

    lateinit var database: FocusDatabase
        private set

    lateinit var focusTimerRepository: FocusTimerRepository
        private set

    lateinit var appBlockRepository: AppBlockRepository
        private set

    lateinit var youTubeStudyRepository: YouTubeStudyRepository
        private set

    lateinit var usageStatsRepository: UsageStatsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        _instance = this

        database = FocusDatabase.getInstance(this)
        focusTimerRepository = FocusTimerRepository(database.focusTimerDao())
        appBlockRepository = AppBlockRepository(database.appBlockDao())
        youTubeStudyRepository = YouTubeStudyRepository(database.studyChannelDao())
        usageStatsRepository = UsageStatsRepository(
            dailyUsageDao = database.dailyUsageDao(),
            focusTimerDao = database.focusTimerDao()
        )
        usageStatsRepository.checkAndRefreshUsageSyncFlag(this)

        focusTimerRepository.onSessionEnded = {
            ProxyLauncherActivity.setComponentEnabled(this, false)
            AppBlockOverlayManager.dismissOverlay()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    usageStatsRepository.syncUsageFromSystem(this@FocusApplication)
                } catch (_: Throwable) {
                }
            }
        }
        // Auto-heal on startup: ensure ProxyLauncherActivity is disabled unless session is active with launcher blocked
        val active = focusTimerRepository.activeSession.value
        if (active == null || active.sessionStatus != SessionStatus.ACTIVE || !active.isLauncherBlocked) {
            ProxyLauncherActivity.setComponentEnabled(this, false)
        }

        FocusStateRepository.initialize(
            context = this,
            existingTimerRepo = focusTimerRepository,
            existingBlockRepo = appBlockRepository,
            existingYouTubeRepo = youTubeStudyRepository,
            existingUsageRepo = usageStatsRepository
        )

        createNotificationChannels()
        seedInitialData()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val monitorChannel = NotificationChannel(
                CHANNEL_MONITORING,
                getString(R.string.monitoring_notification_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.accessibility_service_description)
                setShowBadge(false)
            }

            val sessionChannel = NotificationChannel(
                CHANNEL_SESSION,
                getString(R.string.session_completed_title),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.session_completed_text)
                setShowBadge(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(monitorChannel)
            notificationManager?.createNotificationChannel(sessionChannel)
        }
    }

    private fun seedInitialData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FocusStateRepository.syncInstalledAppsAndDefaults(this@FocusApplication)
            } catch (_: Throwable) {
            }
        }
    }

    companion object {
        const val CHANNEL_MONITORING = "channel_focus_monitoring"
        const val CHANNEL_SESSION = "channel_focus_session"

        @Volatile
        private var _instance: FocusApplication? = null

        val instanceOrNull: FocusApplication?
            get() = _instance

        val instance: FocusApplication
            get() = _instance ?: throw IllegalStateException("FocusApplication not initialized yet")

        fun ensureInitialized(context: Context) {
            val app = (context.applicationContext as? FocusApplication) ?: _instance
            if (app != null) {
                FocusStateRepository.initialize(
                    context = app,
                    existingTimerRepo = if (app::focusTimerRepository.isInitialized) app.focusTimerRepository else null,
                    existingBlockRepo = if (app::appBlockRepository.isInitialized) app.appBlockRepository else null,
                    existingYouTubeRepo = if (app::youTubeStudyRepository.isInitialized) app.youTubeStudyRepository else null,
                    existingUsageRepo = if (app::usageStatsRepository.isInitialized) app.usageStatsRepository else null
                )
            } else {
                FocusStateRepository.initialize(context)
            }
        }
    }
}
