package com.personal.focus.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.personal.focus.FocusApplication
import com.personal.focus.R
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.ui.HomePageActivity
import com.personal.focus.ui.overlay.AppBlockOverlayManager
import com.personal.focus.ui.overlay.OverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Persistent Foreground Watchdog Service running in RAM to monitor
 * app usage limits and enforce focus session blocks via UsageStatsManager
 * and FocusStateRepository.
 */
class MyMonitorService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitorJob: Job? = null
    private var lastInterceptedPackage: String? = null
    private var lastInterceptTime: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        FocusApplication.ensureInitialized(applicationContext)
        startForegroundNotification()
        startWatchdogLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FocusApplication.ensureInitialized(applicationContext)
        startForegroundNotification()
        if (monitorJob == null || monitorJob?.isActive == false) {
            startWatchdogLoop()
        }
        return START_STICKY
    }

    private fun startForegroundNotification() {
        try {
            val launchIntent = Intent(this, HomePageActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                launchIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification: Notification = NotificationCompat.Builder(this, FocusApplication.CHANNEL_MONITORING)
                .setContentTitle(getString(R.string.monitoring_notification_title))
                .setContentText(getString(R.string.monitoring_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (_: Throwable) {
            try {
                val fallbackNotification = NotificationCompat.Builder(this, FocusApplication.CHANNEL_MONITORING)
                    .setContentTitle(getString(R.string.monitoring_notification_title))
                    .setContentText(getString(R.string.monitoring_notification_text))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build()
                startForeground(NOTIFICATION_ID, fallbackNotification)
            } catch (_: Throwable) {
                // Avoid crashing if OS denies background startForeground
            }
        }
    }

    private fun startWatchdogLoop() {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

            while (isActive) {
                try {
                    FocusApplication.ensureInitialized(applicationContext)
                    val blockRepo = FocusStateRepository.appBlockRepository
                    val usageRepo = FocusStateRepository.usageStatsRepository

                    val currentForeground = getForegroundPackage(usageStatsManager)
                    if (currentForeground != null) {
                        if (currentForeground == packageName || isLauncherPackage(currentForeground)) {
                            if ( isLauncherPackage(currentForeground) ) {
                                AppBlockOverlayManager.dismissOverlay()
                            }
                        } else if (isValidTargetPackage(currentForeground)) {
                            val isFocusActive = FocusStateRepository.isSessionActiveNow()

                            // 1. Active Focus Session Enforcement via FocusStateRepository
                            if (isFocusActive && FocusStateRepository.isPackageBlockedDuringFocus(currentForeground)) {
                                triggerOverlay(
                                    targetPackage = currentForeground,
                                    blockReason = OverlayActivity.REASON_FOCUS_ACTIVE
                                )
                            } else {
                                // 2. Daily App Usage Quota Enforcement
                                val limitMinutes = blockRepo?.getDailyLimitMinutesSync(currentForeground) ?: 0
                                if (limitMinutes > 0 && usageRepo != null) {
                                    val usageMs = usageRepo.getPackageTodayUsageMillis(currentForeground)
                                    val limitMs = limitMinutes * 60 * 1000L
                                    if (usageMs >= limitMs) {
                                        triggerOverlay(
                                            targetPackage = currentForeground,
                                            blockReason = OverlayActivity.REASON_LIMIT_EXCEEDED
                                        )
                                    }
                                }
                            }
                        }
                    }
                } catch (_: Throwable) {
                    // Suppress and continue watchdog resilience
                }
                delay(WATCHDOG_INTERVAL_MS)
            }
        }
    }

    private suspend fun getForegroundPackage(usageStatsManager: UsageStatsManager?): String? = withContext(Dispatchers.IO) {
        if (usageStatsManager == null) return@withContext null
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 30_000L

        try {
            val events = usageStatsManager.queryEvents(beginTime, endTime)
            var lastResumedPackage: String? = null
            val event = UsageEvents.Event()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    lastResumedPackage = event.packageName
                }
            }
            if (lastResumedPackage != null) {
                return@withContext lastResumedPackage
            }

            val statsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                endTime - 60_000L,
                endTime
            )
            statsList?.maxByOrNull { it.lastTimeUsed }?.packageName
        } catch (_: Throwable) {
            null
        }
    }

    private fun isValidTargetPackage(packageName: String): Boolean {
        if (packageName == this.packageName) return false
        if (packageName == "com.android.systemui") return false
        if (isLauncherPackage(packageName)) return false
        return true
    }

    private fun isLauncherPackage(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower.contains("launcher") ||
                lower.contains("nexuslauncher") ||
                lower.contains("trebuchet") ||
                lower.contains("home")
    }

    private fun triggerOverlay(targetPackage: String, blockReason: String) {
        val now = System.currentTimeMillis()
        if (targetPackage == lastInterceptedPackage && (now - lastInterceptTime) < 1200L) {
            return
        }
        lastInterceptedPackage = targetPackage
        lastInterceptTime = now

        // 1. Direct WindowManager system overlay fallback (TYPE_APPLICATION_OVERLAY)
        AppBlockOverlayManager.showOverlay(this, targetPackage, blockReason)

        // 2. Launch OverlayActivity with required flags
        try {
            val overlayIntent = Intent(this, OverlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
                putExtra(OverlayActivity.EXTRA_PACKAGE_NAME, targetPackage)
                putExtra(OverlayActivity.EXTRA_BLOCK_REASON, blockReason)
            }
            startActivity(overlayIntent)
        } catch (_: Throwable) {
            // Background Activity Launch restriction on Android 14+ handled by WindowManager overlay
        }
    }

    override fun onDestroy() {
        monitorJob?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val WATCHDOG_INTERVAL_MS = 500L

        fun startService(context: Context) {
            try {
                val intent = Intent(context, MyMonitorService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Throwable) {
            }
        }
    }
}
