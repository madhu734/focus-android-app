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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private var pauseElapsedRealtimeStart: Long? = null

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
                    if (current.isInBreak && current.breakExpiryTime != null) {
                        val breakDuration = current.breakDurationInMillis
                        val breakStartElapsed = breakElapsedRealtimeStart ?: (
                            timeProvider() - (breakDuration - (current.breakExpiryTime - wallClockProvider()).coerceAtLeast(0L))
                        ).also { breakElapsedRealtimeStart = it }
                        val breakElapsed = (timeProvider() - breakStartElapsed).coerceAtLeast(0L)
                        val breakRemaining = (breakDuration - breakElapsed).coerceAtLeast(0L)
                        _remainingTimeMillis.value = breakRemaining
                        if (breakRemaining <= 0L) {
                            endBreak()
                        }
                    } else if (current.durationInMillis != null) {
                        val elapsedMs = (timeProvider() - current.elapsedRealtimeStart).coerceAtLeast(0L)
                        val remaining = (current.durationInMillis - elapsedMs).coerceAtLeast(0L)
                        _remainingTimeMillis.value = remaining
                        if (remaining <= 0L) {
                            completeSession()
                            break
                        }
                    } else {
                        // Stopwatch mode: tracks elapsed milliseconds
                        val elapsedMs = (timeProvider() - current.elapsedRealtimeStart).coerceAtLeast(0L)
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
        breakElapsedRealtimeStart = null
        pauseElapsedRealtimeStart = null

        val now = wallClockProvider()
        val elapsedRealtime = timeProvider()
        val effectiveBreaksAllowed = if (isLockedIn) 0 else breaksAllowedCount.coerceIn(0, 4)

        val session = FocusTimerSession(
            startTime = now,
            durationInMillis = durationInMillis,
            mode = mode,
            isLockedIn = isLockedIn,
            disableEndButtonShown = disableEndButtonShown,
            isUninstallBlocked = isUninstallBlocked,
            isLauncherBlocked = isLauncherBlocked,
            blockNotifications = blockNotifications,
            breaksAllowedCount = effectiveBreaksAllowed,
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
        _remainingTimeMillis.value = durationInMillis ?: 0L
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
        if (current.sessionStatus == SessionStatus.PAUSED) return true
        pauseElapsedRealtimeStart = timeProvider()
        val updated = current.copy(sessionStatus = SessionStatus.PAUSED)
        _activeSession.value = updated
        focusTimerDao.updateSession(updated)
        return true
    }

    suspend fun resumeSession(): Boolean {
        val current = activeSession.value ?: return false
        if (current.sessionStatus != SessionStatus.PAUSED) return false
        val pauseSpent = pauseElapsedRealtimeStart?.let { (timeProvider() - it).coerceAtLeast(0L) } ?: 0L
        pauseElapsedRealtimeStart = null
        val updated = current.copy(
            sessionStatus = SessionStatus.ACTIVE,
            elapsedRealtimeStart = current.elapsedRealtimeStart + pauseSpent,
            startTime = current.startTime + pauseSpent
        )
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
        if (current.isInBreak) {
            return false
        }
        if (current.breaksTakenCount >= current.breaksAllowedCount) {
            return false
        }

        val nowRealtime = timeProvider()
        val pauseSpent = if (current.sessionStatus == SessionStatus.PAUSED) {
            pauseElapsedRealtimeStart?.let { (nowRealtime - it).coerceAtLeast(0L) } ?: 0L
        } else {
            0L
        }
        pauseElapsedRealtimeStart = null

        val breakDuration = current.breakDurationInMillis
        val expiry = wallClockProvider() + breakDuration
        breakElapsedRealtimeStart = nowRealtime

        val updated = current.copy(
            sessionStatus = SessionStatus.ACTIVE,
            isInBreak = true,
            breaksTakenCount = current.breaksTakenCount + 1,
            breakExpiryTime = expiry,
            elapsedRealtimeStart = current.elapsedRealtimeStart + pauseSpent,
            startTime = current.startTime + pauseSpent
        )
        _activeSession.value = updated
        _remainingTimeMillis.value = breakDuration
        focusTimerDao.updateSession(updated)
        return true
    }

    suspend fun endBreak() {
        val current = activeSession.value ?: return
        if (!current.isInBreak) return

        val nowRealtime = timeProvider()
        val breakSpent = breakElapsedRealtimeStart?.let { (nowRealtime - it).coerceAtLeast(0L) } ?: 0L
        breakElapsedRealtimeStart = null

        val newElapsedStart = current.elapsedRealtimeStart + breakSpent
        val newWallStart = current.startTime + breakSpent

        val updated = current.copy(
            isInBreak = false,
            breakExpiryTime = null,
            currentRound = current.currentRound + 1,
            elapsedRealtimeStart = newElapsedStart,
            startTime = newWallStart
        )
        _activeSession.value = updated
        val elapsedFocusMs = (nowRealtime - newElapsedStart).coerceAtLeast(0L)
        _remainingTimeMillis.value = if (updated.durationInMillis != null) {
            (updated.durationInMillis - elapsedFocusMs).coerceAtLeast(0L)
        } else {
            elapsedFocusMs
        }
        focusTimerDao.updateSession(updated)
    }

    suspend fun giveUpSession(): Result<Unit> {
        val current = activeSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        // Anti-give-up check in Strict Mode
        if (current.isLockedIn && current.disableEndButtonShown) {
            return Result.failure(IllegalStateException("Can't turn off while Strict Mode is active."))
        }

        val nowRealtime = timeProvider()
        val pauseSpent = if (current.sessionStatus == SessionStatus.PAUSED) {
            pauseElapsedRealtimeStart?.let { (nowRealtime - it).coerceAtLeast(0L) } ?: 0L
        } else {
            0L
        }
        val breakSpent = if (current.isInBreak) {
            breakElapsedRealtimeStart?.let { (nowRealtime - it).coerceAtLeast(0L) } ?: 0L
        } else {
            0L
        }
        breakElapsedRealtimeStart = null
        pauseElapsedRealtimeStart = null

        val rawElapsedMs = (nowRealtime - current.elapsedRealtimeStart - pauseSpent - breakSpent).coerceAtLeast(0L)
        val elapsedMs = if (current.durationInMillis != null) {
            rawElapsedMs.coerceAtMost(current.durationInMillis)
        } else {
            rawElapsedMs
        }
        val updated = current.copy(
            sessionStatus = SessionStatus.CANCELLED,
            isInBreak = false,
            breakExpiryTime = null,
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
        pauseElapsedRealtimeStart = null
        val elapsedMs = (timeProvider() - current.elapsedRealtimeStart).coerceAtLeast(0L)
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
