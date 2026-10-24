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

import android.app.NotificationManager
import com.personal.focus.data.model.FocusMode
import com.personal.focus.data.model.FocusTimerSession
import com.personal.focus.data.model.SessionStatus
import kotlinx.coroutines.flow.combine

/**
 * Persistent Foreground Watchdog Service running in RAM to monitor
 * app usage limits, display live VPN-style focus timer notifications,
 * and enforce focus session blocks via UsageStatsManager and FocusStateRepository.
 */
class MyMonitorService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitorJob: Job? = null
    private var notificationTickerJob: Job? = null
    private var lastInterceptedPackage: String? = null
    private var lastInterceptTime: Long = 0L
    private var lastNotifiedSecond: Long = -1L
    private var lastNotifiedStatus: String = ""

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        FocusApplication.ensureInitialized(applicationContext)
        startForegroundNotification()
        startLiveNotificationObserver()
        startWatchdogLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FocusApplication.ensureInitialized(applicationContext)
        startForegroundNotification()
        if (notificationTickerJob == null || notificationTickerJob?.isActive == false) {
            startLiveNotificationObserver()
        }
        if (monitorJob == null || monitorJob?.isActive == false) {
            startWatchdogLoop()
        }
        return START_STICKY
    }

    private fun startLiveNotificationObserver() {
        notificationTickerJob?.cancel()
        notificationTickerJob = serviceScope.launch {
            combine(
                FocusStateRepository.activeSession,
                FocusStateRepository.remainingTimeMillis
            ) { session, remainingMs ->
                session to remainingMs
            }.collect { (session, remainingMs) ->
                val currentSecond = remainingMs / 1000L
                val statusKey = "${session?.sessionId}_${session?.sessionStatus}_${session?.isInBreak}_${session?.mode}"
                if (currentSecond != lastNotifiedSecond || statusKey != lastNotifiedStatus) {
                    lastNotifiedSecond = currentSecond
                    lastNotifiedStatus = statusKey
                    updateLiveNotification(session, remainingMs)
                }
            }
        }
    }

    private fun buildServiceNotification(
        session: FocusTimerSession?,
        remainingMillis: Long
    ): Notification {
        val launchIntent = Intent(this, HomePageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isActiveOrPaused = session != null &&
                (session.sessionStatus == SessionStatus.ACTIVE || session.sessionStatus == SessionStatus.PAUSED)

        val contentTitle: String
        val contentText: String
        var progressMax = 0
        var progressCurrent = 0

        if (isActiveOrPaused && session != null) {
            val totalSeconds = (remainingMillis / 1000L).coerceAtLeast(0L)
            val hours = totalSeconds / 3600L
            val minutes = (totalSeconds % 3600L) / 60L
            val seconds = totalSeconds % 60L
            val formattedTimer = if (hours > 0L) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }

            val modeLabel = when (session.mode) {
                FocusMode.COUNTDOWN -> "Countdown"
                FocusMode.STOPWATCH -> "Stopwatch"
                FocusMode.POMODORO -> "Pomodoro"
            }

            contentTitle = when {
                session.isInBreak -> "☕ Short Break • $formattedTimer remaining"
                session.sessionStatus == SessionStatus.PAUSED -> "⏸️ Focus Session Paused • $formattedTimer"
                session.mode == FocusMode.STOPWATCH -> "⏱️ Stopwatch Focus • $formattedTimer elapsed"
                else -> "⚡ Focus Session Active • $formattedTimer remaining"
            }

            contentText = when {
                session.isInBreak -> "$modeLabel Mode • Recharge and get ready"
                session.isLockedIn -> "🔒 Strict Mode Locked • Stay focused!"
                else -> "$modeLabel Mode • Distracting apps are blocked 🔒"
            }

            when {
                session.isInBreak -> {
                    val maxSec = (session.breakDurationInMillis / 1000L).toInt().coerceAtLeast(1)
                    progressMax = maxSec
                    progressCurrent = totalSeconds.toInt().coerceIn(0, maxSec)
                }
                session.mode != FocusMode.STOPWATCH -> {
                    val maxSec = ((session.durationInMillis ?: 0L) / 1000L).toInt().coerceAtLeast(1)
                    progressMax = maxSec
                    progressCurrent = totalSeconds.toInt().coerceIn(0, maxSec)
                }
                else -> {
                    progressMax = 0
                    progressCurrent = 0
                }
            }
        } else {
            contentTitle = "🛡️ Focus Protection Active"
            contentText = getString(R.string.monitoring_notification_text)
        }

        // VPN-Style persistent ongoing notification without any Stop/Cancel/Give Up action buttons
        return NotificationCompat.Builder(this, FocusApplication.CHANNEL_MONITORING)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColor(0xFF83D65C.toInt())
            .setColorized(true)
            .setProgress(progressMax, progressCurrent, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun updateLiveNotification(session: FocusTimerSession?, remainingMillis: Long) {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val notification = buildServiceNotification(session, remainingMillis)
            notificationManager?.notify(NOTIFICATION_ID, notification)
        } catch (_: Throwable) {
        }
    }

    private fun startForegroundNotification() {
        try {
            val session = FocusStateRepository.activeSession.value
            val remainingMs = FocusStateRepository.remainingTimeMillis.value
            val notification = buildServiceNotification(session, remainingMs)

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
                    .setContentTitle("🛡️ Focus Protection Active")
                    .setContentText(getString(R.string.monitoring_notification_text))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(0xFF83D65C.toInt())
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setSilent(true)
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

                    val currentForeground = getForegroundPackage(usageStatsManager)
                    if (currentForeground != null) {
                        if (currentForeground == packageName || isLauncherPackage(currentForeground)) {
                            if ( isLauncherPackage(currentForeground) ) {
                                AppBlockOverlayManager.dismissOverlay()
                            }
                        } else if (isValidTargetPackage(currentForeground)) {
                            val isFocusActive = FocusStateRepository.isSessionActiveNow()

                            // Active Focus Session Enforcement ONLY (Daily Screentime Limit removed)
                            if (isFocusActive && FocusStateRepository.isPackageBlockedDuringFocus(currentForeground)) {
                                triggerOverlay(
                                    targetPackage = currentForeground,
                                    blockReason = OverlayActivity.REASON_FOCUS_ACTIVE
                                )
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
        notificationTickerJob?.cancel()
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
