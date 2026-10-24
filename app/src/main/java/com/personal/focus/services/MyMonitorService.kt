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
import com.personal.focus.R
import com.personal.focus.FocusApplication
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.ui.HomePageActivity
import com.personal.focus.ui.overlay.AppBlockOverlayManager
import com.personal.focus.ui.overlay.OverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Persistent Foreground Watchdog Service running in RAM to monitor
 * app usage limits and enforce focus session blocks via UsageStatsManager.
 */
class MyMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var monitorJob: Job? = null
    private var lastInterceptedPackage: String? = null
    private var lastInterceptTime: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()
        startWatchdogLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundNotification()
        if (monitorJob == null || monitorJob?.isActive == false) {
            startWatchdogLoop()
        }
        return START_STICKY
    }

    private fun startForegroundNotification() {
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

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startWatchdogLoop() {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val app = FocusApplication.instance
            val focusRepo = app.focusTimerRepository
            val blockRepo = app.appBlockRepository
            val usageRepo = app.usageStatsRepository
            val ytRepo = app.youTubeStudyRepository

            while (isActive) {
                try {
                    val currentForeground = getForegroundPackage(usageStatsManager)
                    if (currentForeground != null) {
                        if (currentForeground == packageName || isLauncherPackage(currentForeground)) {
                            // User is on home screen or in Focus - dismiss blocking overlay
                            AppBlockOverlayManager.dismissOverlay()
                        } else if (isValidTargetPackage(currentForeground)) {
                            val activeSession = focusRepo.activeSession.value
                            val isFocusActive = activeSession != null &&
                                    activeSession.sessionStatus == SessionStatus.ACTIVE &&
                                    !activeSession.isInBreak

                            // 1. Focus Session Enforcement
                            if (isFocusActive) {
                                val isYouTube = (currentForeground == "com.google.android.youtube")
                                val isStudyModeActive = isYouTube && (ytRepo.isStudyModeEnabled.value || isFocusActive) && MyMonitorInAppBlockService.isAlive

                                if (!isStudyModeActive && blockRepo.isBlockedDuringFocus(currentForeground)) {
                                    triggerOverlay(
                                        targetPackage = currentForeground,
                                        blockReason = OverlayActivity.REASON_FOCUS_ACTIVE
                                    )
                                }
                            } else {
                                // Permanent App Blocklist enforcement outside focus session
                                if (blockRepo.isAppBlockedSync(currentForeground)) {
                                    triggerOverlay(
                                        targetPackage = currentForeground,
                                        blockReason = OverlayActivity.REASON_APP_BLOCKED
                                    )
                                }
                            }

                            // 2. Daily App Usage Quota Enforcement
                            val limitMinutes = blockRepo.getDailyLimitMinutesSync(currentForeground)
                            if (limitMinutes > 0) {
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
                } catch (e: Exception) {
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

            // Fallback to queryUsageStats if no recent resumed event in window
            val statsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                endTime - 60_000L,
                endTime
            )
            statsList.maxByOrNull { it.lastTimeUsed }?.packageName
        } catch (e: Exception) {
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

    private fun isCommonDistraction(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower.contains("youtube") ||
                lower.contains("chrome") ||
                lower.contains("browser") ||
                lower.contains("instagram") ||
                lower.contains("tiktok") ||
                lower.contains("facebook") ||
                lower.contains("whatsapp") ||
                lower.contains("twitter") ||
                lower.contains("snapchat") ||
                lower.contains("reddit") ||
                lower.contains("netflix")
    }

    private fun triggerOverlay(targetPackage: String, blockReason: String) {
        val now = System.currentTimeMillis()
        // Prevent rapid duplicate launches within 1.5 seconds for the same package
        if (targetPackage == lastInterceptedPackage && (now - lastInterceptTime) < 1500L) {
            return
        }
        lastInterceptedPackage = targetPackage
        lastInterceptTime = now

        // 1. Instant full-screen Compose/View blocking overlay directly via WindowManager
        // This is immune to Android 14+ Background Activity Launch (BAL) restrictions
        AppBlockOverlayManager.showOverlay(this, targetPackage, blockReason)

        // 2. Also attempt starting OverlayActivity as secondary interception
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
        } catch (e: Exception) {
            // Background Activity Launch restriction on Android 14+ handled by WindowManager overlay
        }
    }

    override fun onDestroy() {
        monitorJob?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val WATCHDOG_INTERVAL_MS = 1000L

        fun startService(context: Context) {
            val intent = Intent(context, MyMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
