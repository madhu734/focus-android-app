package com.personal.focus.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.personal.focus.FocusApplication
import com.personal.focus.data.repository.FocusStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Zero-latency Notification Interception Service.
 * Suppresses and cancels notifications from distracting applications
 * during active Ultra Focus sessions while preserving essential calls and ongoing services.
 */
class NotificationListener : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        if (sbn == null) return
        val packageName = sbn.packageName ?: return

        // 1. Safety Invariant: Never block Focus's own notifications
        if (packageName == applicationContext.packageName) return

        val notification = sbn.notification ?: return
        val flags = notification.flags

        // 2. Safety Invariant: Never block phone calls, ongoing events, or foreground services
        val isOngoing = (flags and Notification.FLAG_ONGOING_EVENT) != 0
        val isForegroundService = (flags and Notification.FLAG_FOREGROUND_SERVICE) != 0
        val isPhoneCall = notification.category == Notification.CATEGORY_CALL ||
                notification.category == Notification.CATEGORY_ALARM

        if (isOngoing || isForegroundService || isPhoneCall) {
            return
        }

        // 3. Query active focus session blocking policy via FocusStateRepository
        serviceScope.launch {
            try {
                FocusApplication.ensureInitialized(applicationContext)
                val activeSession = FocusStateRepository.activeSession.value
                val isFocusActive = FocusStateRepository.isSessionActiveNow()

                if (isFocusActive && activeSession?.blockNotifications == true) {
                    if (FocusStateRepository.isPackageBlockedDuringFocus(packageName)) {
                        cancelNotification(sbn.key)
                    }
                }
            } catch (_: Throwable) {
                // Fail-safe: don't crash the listener
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
