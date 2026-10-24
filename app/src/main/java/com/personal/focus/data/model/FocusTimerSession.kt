package com.personal.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Focus session runtime model matching Section 3.1 of CLEAN_ARCHITECTURE.md.
 */
@Entity(tableName = "focus_timer_session")
data class FocusTimerSession(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,
    val startTime: Long,                     // Epoch millis of session start
    val durationInMillis: Long?,             // Total planned duration (null for stopwatch)
    val mode: FocusMode,                     // POMODORO | STOPWATCH | COUNTDOWN
    val isLockedIn: Boolean,                 // STRICT MODE ACTIVE FLAG
    val disableEndButtonShown: Boolean,      // Hides "Give Up" / "End" button
    val isUninstallBlocked: Boolean,         // Enforces DeviceAdmin & Settings protection
    val isLauncherBlocked: Boolean,          // Enforces Home screen lockout
    val blockNotifications: Boolean,         // Triggers NotificationListener suppression
    val breaksAllowedCount: Int,             // Max breaks permitted (e.g. 0 for Strict Mode)
    val breaksTakenCount: Int = 0,
    val breakDurationInMillis: Long,         // Short break length (e.g. 5 mins = 300_000L)
    val longBreakDurationInMillis: Long,     // Long break length (e.g. 15 mins = 900_000L)
    val roundsPerCycle: Int = 4,             // Pomodoro rounds before long break
    val currentRound: Int = 1,
    val isInBreak: Boolean = false,
    val breakExpiryTime: Long? = null,       // If currently in break, timestamp when break ends
    val sessionStatus: SessionStatus = SessionStatus.ACTIVE,
    val elapsedRealtimeStart: Long,          // SystemClock.elapsedRealtime() for tampering defense
    val totalFocusTimeSpentMillis: Long = 0
)
