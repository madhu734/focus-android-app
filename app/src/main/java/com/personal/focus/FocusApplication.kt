package com.personal.focus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.personal.focus.data.FocusDatabase
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.data.repository.AppBlockRepository
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
        instance = this

        database = FocusDatabase.getInstance(this)
        focusTimerRepository = FocusTimerRepository(database.focusTimerDao())
        focusTimerRepository.onSessionEnded = {
            ProxyLauncherActivity.setComponentEnabled(this, false)
            AppBlockOverlayManager.dismissOverlay()
        }
        // Auto-heal on startup: ensure ProxyLauncherActivity is disabled unless session is active with launcher blocked
        val active = focusTimerRepository.activeSession.value
        if (active == null || active.sessionStatus != SessionStatus.ACTIVE || !active.isLauncherBlocked) {
            ProxyLauncherActivity.setComponentEnabled(this, false)
        }
        appBlockRepository = AppBlockRepository(database.appBlockDao())
        youTubeStudyRepository = YouTubeStudyRepository(database.studyChannelDao())
        usageStatsRepository = UsageStatsRepository(database.dailyUsageDao())

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
            youTubeStudyRepository.seedDefaultsIfEmpty()
            
            // Seed common distracting apps for initial picker
            val defaultDistractions = listOf(
                "com.google.android.youtube" to "YouTube",
                "com.android.chrome" to "Google Chrome",
                "com.whatsapp" to "WhatsApp",
                "com.instagram.android" to "Instagram",
                "com.facebook.katana" to "Facebook",
                "com.zhiliaoapp.musically" to "TikTok",
                "com.twitter.android" to "X (Twitter)",
                "com.snapchat.android" to "Snapchat",
                "com.reddit.frontpage" to "Reddit",
                "com.netflix.mediaclient" to "Netflix"
            )
            appBlockRepository.seedDefaultsIfNeeded(defaultDistractions)
        }
    }

    companion object {
        const val CHANNEL_MONITORING = "channel_focus_monitoring"
        const val CHANNEL_SESSION = "channel_focus_session"

        lateinit var instance: FocusApplication
            private set
    }
}
