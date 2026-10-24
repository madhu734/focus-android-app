package com.personal.focus.data.repository

import android.os.SystemClock
import com.personal.focus.data.dao.FocusTimerDao
import com.personal.focus.data.model.FocusMode
import com.personal.focus.data.model.FocusTimerSession
import com.personal.focus.data.model.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Repository managing FocusTimerSessions, strict lockouts, breaks,
 * and clock tampering prevention.
 */
class FocusTimerRepository(
    private val focusTimerDao: FocusTimerDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val timeProvider: () -> Long = { SystemClock.elapsedRealtime() },
    private val wallClockProvider: () -> Long = { System.currentTimeMillis() }
) {
    private val _activeSession = MutableStateFlow<FocusTimerSession?>(null)
    val activeSession: StateFlow<FocusTimerSession?> = _activeSession.asStateFlow()

    private val _remainingTimeMillis = MutableStateFlow(0L)
    val remainingTimeMillis: StateFlow<Long> = _remainingTimeMillis.asStateFlow()

    private val _isStrictActive = MutableStateFlow(false)
    val isStrictActive: StateFlow<Boolean> = _isStrictActive.asStateFlow()

    private val _isTimeTampered = MutableStateFlow(false)
    val isTimeTampered: StateFlow<Boolean> = _isTimeTampered.asStateFlow()

    var onSessionEnded: (() -> Unit)? = null
    private var breakElapsedRealtimeStart: Long? = null

    private var tickerJob: Job? = null

    init {
        scope.launch {
            focusTimerDao.getActiveSessionFlow().collect { dbSession ->
                if (_activeSession.value != dbSession) {
                    _activeSession.value = dbSession
                }
            }
        }

        scope.launch {
            activeSession.collect { session ->
                if (session != null && (session.sessionStatus == SessionStatus.ACTIVE || session.sessionStatus == SessionStatus.PAUSED)) {
                    _isStrictActive.value = session.isLockedIn
                    startTicker(session)
                } else {
                    _isStrictActive.value = false
                    stopTicker()
                    _remainingTimeMillis.value = 0L
                    onSessionEnded?.invoke()
                }
            }
        }
    }

    private fun startTicker(session: FocusTimerSession) {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                val current = activeSession.value ?: break
                if (current.sessionStatus == SessionStatus.ACTIVE) {
                    val elapsedMs = timeProvider() - current.elapsedRealtimeStart
                    
                    if (current.isInBreak && current.breakExpiryTime != null) {
                        val breakDuration = if (current.currentRound % current.roundsPerCycle == 0) {
                            current.longBreakDurationInMillis
                        } else {
                            current.breakDurationInMillis
                        }
                        val breakStartElapsed = breakElapsedRealtimeStart ?: (timeProvider() - (breakDuration - (current.breakExpiryTime - System.currentTimeMillis()).coerceAtLeast(0L)))
                        val breakElapsed = timeProvider() - breakStartElapsed
                        val breakRemaining = (breakDuration - breakElapsed).coerceAtLeast(0L)
                        _remainingTimeMillis.value = breakRemaining
                        if (breakRemaining <= 0L) {
                            endBreak()
                        }
                    } else if (current.durationInMillis != null) {
                        val remaining = (current.durationInMillis - elapsedMs).coerceAtLeast(0L)
                        _remainingTimeMillis.value = remaining
                        if (remaining <= 0L) {
                            completeSession()
                            break
                        }
                    } else {
                        // Stopwatch mode: tracks elapsed milliseconds
                        _remainingTimeMillis.value = elapsedMs
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    suspend fun startSession(
        mode: FocusMode,
        durationInMillis: Long?,
        isLockedIn: Boolean,
        disableEndButtonShown: Boolean,
        isUninstallBlocked: Boolean,
        isLauncherBlocked: Boolean,
        blockNotifications: Boolean,
        breaksAllowedCount: Int,
        breakDurationInMillis: Long = 5 * 60 * 1000L,
        longBreakDurationInMillis: Long = 15 * 60 * 1000L,
        roundsPerCycle: Int = 4
    ): Long {
        // Cancel any lingering sessions first
        focusTimerDao.cancelAllActiveSessions()

        val now = wallClockProvider()
        val elapsedRealtime = timeProvider()

        val session = FocusTimerSession(
            startTime = now,
            durationInMillis = durationInMillis,
            mode = mode,
            isLockedIn = isLockedIn,
            disableEndButtonShown = disableEndButtonShown,
            isUninstallBlocked = isUninstallBlocked,
            isLauncherBlocked = isLauncherBlocked,
            blockNotifications = blockNotifications,
            breaksAllowedCount = if (isLockedIn) 0 else breaksAllowedCount,
            breaksTakenCount = 0,
            breakDurationInMillis = breakDurationInMillis,
            longBreakDurationInMillis = longBreakDurationInMillis,
            roundsPerCycle = roundsPerCycle,
            currentRound = 1,
            isInBreak = false,
            breakExpiryTime = null,
            sessionStatus = SessionStatus.ACTIVE,
            elapsedRealtimeStart = elapsedRealtime,
            totalFocusTimeSpentMillis = 0L
        )

        _activeSession.value = session
        _isStrictActive.value = isLockedIn
        val id = focusTimerDao.insertSession(session)
        val withId = session.copy(sessionId = id)
        _activeSession.value = withId
        return id
    }

    suspend fun pauseSession(): Boolean {
        val current = activeSession.value ?: return false
        if (current.isLockedIn) {
            // Strict mode does not permit pausing
            return false
        }
        val updated = current.copy(sessionStatus = SessionStatus.PAUSED)
        _activeSession.value = updated
        focusTimerDao.updateSession(updated)
        return true
    }

    suspend fun resumeSession(): Boolean {
        val current = activeSession.value ?: return false
        if (current.sessionStatus != SessionStatus.PAUSED) return false
        val updated = current.copy(sessionStatus = SessionStatus.ACTIVE)
        _activeSession.value = updated
        focusTimerDao.updateSession(updated)
        return true
    }

    suspend fun takeBreak(): Boolean {
        val current = activeSession.value ?: return false
        if (current.isLockedIn) {
            // Strict mode: breaks forbidden
            return false
        }
        if (current.breaksTakenCount >= current.breaksAllowedCount) {
            return false
        }

        val isLongBreak = (current.currentRound % current.roundsPerCycle == 0)
        val breakDuration = if (isLongBreak) current.longBreakDurationInMillis else current.breakDurationInMillis
        val expiry = wallClockProvider() + breakDuration
        breakElapsedRealtimeStart = timeProvider()

        val updated = current.copy(
            isInBreak = true,
            breaksTakenCount = current.breaksTakenCount + 1,
            breakExpiryTime = expiry
        )
        _activeSession.value = updated
        focusTimerDao.updateSession(updated)
        return true
    }

    suspend fun endBreak() {
        val current = activeSession.value ?: return
        if (!current.isInBreak) return

        breakElapsedRealtimeStart = null
        val updated = current.copy(
            isInBreak = false,
            breakExpiryTime = null,
            currentRound = current.currentRound + 1
        )
        _activeSession.value = updated
        focusTimerDao.updateSession(updated)
    }

    suspend fun giveUpSession(): Result<Unit> {
        val current = activeSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        // Anti-give-up check in Strict Mode
        if (current.isLockedIn && current.disableEndButtonShown) {
            return Result.failure(IllegalStateException("Can't turn off while Strict Mode is active."))
        }

        breakElapsedRealtimeStart = null
        val elapsedMs = timeProvider() - current.elapsedRealtimeStart
        val updated = current.copy(
            sessionStatus = SessionStatus.CANCELLED,
            totalFocusTimeSpentMillis = elapsedMs
        )
        _activeSession.value = updated
        _isStrictActive.value = false
        focusTimerDao.updateSession(updated)
        stopTicker()
        onSessionEnded?.invoke()
        return Result.success(Unit)
    }

    suspend fun completeSession() {
        val current = activeSession.value ?: return
        breakElapsedRealtimeStart = null
        val elapsedMs = timeProvider() - current.elapsedRealtimeStart
        val updated = current.copy(
            sessionStatus = SessionStatus.COMPLETED,
            totalFocusTimeSpentMillis = current.durationInMillis ?: elapsedMs
        )
        _activeSession.value = updated
        _isStrictActive.value = false
        focusTimerDao.updateSession(updated)
        stopTicker()
        onSessionEnded?.invoke()
    }

    /**
     * Anti-tampering check: verifies that wall clock matches elapsed realtime bounds.
     */
    fun verifyTimeIntegrity(): Boolean {
        val current = activeSession.value ?: return true
        val wallElapsed = wallClockProvider() - current.startTime
        val realtimeElapsed = timeProvider() - current.elapsedRealtimeStart

        // If difference is greater than 60 seconds, system clock was modified!
        val discrepancy = Math.abs(wallElapsed - realtimeElapsed)
        val isTampered = discrepancy >= 60_000L
        _isTimeTampered.value = isTampered
        return !isTampered
    }
}
