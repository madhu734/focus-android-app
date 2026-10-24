package com.personal.focus

import com.personal.focus.data.dao.FocusTimerDao
import com.personal.focus.data.model.FocusMode
import com.personal.focus.data.model.FocusTimerSession
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.data.repository.FocusTimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeFocusTimerDao : FocusTimerDao {
    private val sessionFlow = MutableStateFlow<FocusTimerSession?>(null)
    private val allSessionsFlow = MutableStateFlow<List<FocusTimerSession>>(emptyList())
    private var currentSession: FocusTimerSession? = null

    override fun getActiveSessionFlow(): Flow<FocusTimerSession?> = sessionFlow

    override suspend fun getActiveSession(): FocusTimerSession? = currentSession

    override fun getLatestSessionFlow(): Flow<FocusTimerSession?> = sessionFlow

    override fun getAllSessionsFlow(): Flow<List<FocusTimerSession>> = allSessionsFlow

    override suspend fun insertSession(session: FocusTimerSession): Long {
        currentSession = session
        sessionFlow.value = session
        allSessionsFlow.value = listOf(session)
        return 1L
    }

    override suspend fun updateSession(session: FocusTimerSession) {
        currentSession = session
        sessionFlow.value = session
        allSessionsFlow.value = listOf(session)
    }

    override suspend fun cancelAllActiveSessions() {
        if (currentSession?.sessionStatus == SessionStatus.ACTIVE || currentSession?.sessionStatus == SessionStatus.PAUSED) {
            val updated = currentSession?.copy(sessionStatus = SessionStatus.CANCELLED)
            currentSession = updated
            sessionFlow.value = updated
            allSessionsFlow.value = if (updated != null) listOf(updated) else emptyList()
        }
    }

    override fun getTotalCompletedFocusTimeMillisFlow(): Flow<Long?> =
        MutableStateFlow(1000L)
}

class FocusTimerRepositoryTest {

    @Test
    fun `test strict mode rejects give up and pause and break`() = runTest {
        val fakeDao = FakeFocusTimerDao()
        var mockTime = 100_000L
        val repository = FocusTimerRepository(fakeDao, backgroundScope, timeProvider = { mockTime })

        // Start session with Strict Mode
        repository.startSession(
            mode = FocusMode.COUNTDOWN,
            durationInMillis = 120 * 60 * 1000L,
            isLockedIn = true,
            disableEndButtonShown = true,
            isUninstallBlocked = true,
            isLauncherBlocked = true,
            blockNotifications = true,
            breaksAllowedCount = 4
        )
        testScheduler.advanceUntilIdle()

        val active = repository.activeSession.value
        assertNotNull(active)
        assertTrue(active!!.isLockedIn)
        assertEquals(0, active.breaksAllowedCount)
        assertTrue(active.disableEndButtonShown)
        assertTrue(repository.isStrictActive.value)

        // 1. Strict Mode forbids pausing
        val paused = repository.pauseSession()
        assertFalse(paused)

        // 2. Strict Mode forbids taking breaks
        val tookBreak = repository.takeBreak()
        assertFalse(tookBreak)

        // 3. Strict Mode rejects give-up
        val giveUpResult = repository.giveUpSession()
        assertTrue(giveUpResult.isFailure)
        assertEquals("Can't turn off while Strict Mode is active.", giveUpResult.exceptionOrNull()?.message)
    }

    @Test
    fun `test standard focus mode permits up to 4 breaks and preserves focus duration across break`() = runTest {
        val fakeDao = FakeFocusTimerDao()
        var mockTime = 100_000L
        var mockWall = 1_000_000L
        val repository = FocusTimerRepository(
            fakeDao,
            backgroundScope,
            timeProvider = { mockTime },
            wallClockProvider = { mockWall }
        )

        val focusDuration = 120 * 60 * 1000L // 120m support
        repository.startSession(
            mode = FocusMode.COUNTDOWN,
            durationInMillis = focusDuration,
            isLockedIn = false,
            disableEndButtonShown = false,
            isUninstallBlocked = false,
            isLauncherBlocked = false,
            blockNotifications = false,
            breaksAllowedCount = 4
        )
        testScheduler.advanceUntilIdle()

        // Advance 10 minutes into focus session
        mockTime += 10 * 60 * 1000L
        mockWall += 10 * 60 * 1000L

        // Take Break 1 (5m short break)
        assertTrue(repository.takeBreak())
        assertTrue(repository.activeSession.value?.isInBreak == true)
        assertEquals(1, repository.activeSession.value?.breaksTakenCount)
        assertEquals(5 * 60 * 1000L, repository.remainingTimeMillis.value)

        // Spend 5 minutes in break
        mockTime += 5 * 60 * 1000L
        mockWall += 5 * 60 * 1000L

        // End break -> focus timer must resume with 110m remaining (break time not subtracted from focus)
        repository.endBreak()
        assertFalse(repository.activeSession.value?.isInBreak == true)
        assertEquals(110 * 60 * 1000L, repository.remainingTimeMillis.value)
        assertTrue(repository.verifyTimeIntegrity())

        // Give up succeeds in standard mode
        val giveUpResult = repository.giveUpSession()
        assertTrue(giveUpResult.isSuccess)
        assertEquals(SessionStatus.CANCELLED, repository.activeSession.value?.sessionStatus)
    }

    @Test
    fun `test onSessionEnded callback triggers on completion and give up`() = runTest {
        val fakeDao = FakeFocusTimerDao()
        var mockTime = 100_000L
        val repository = FocusTimerRepository(fakeDao, backgroundScope, timeProvider = { mockTime })
        var sessionEndedTriggered = false
        repository.onSessionEnded = { sessionEndedTriggered = true }

        repository.startSession(
            mode = FocusMode.POMODORO,
            durationInMillis = 25 * 60 * 1000L,
            isLockedIn = false,
            disableEndButtonShown = false,
            isUninstallBlocked = false,
            isLauncherBlocked = true,
            blockNotifications = false,
            breaksAllowedCount = 1
        )
        testScheduler.advanceUntilIdle()

        repository.giveUpSession()
        testScheduler.advanceUntilIdle()

        assertTrue(sessionEndedTriggered)
    }

    @Test
    fun `test time integrity check detects wall clock tampering`() = runTest {
        val fakeDao = FakeFocusTimerDao()
        var mockRealtime = 50_000L
        var mockWallClock = 1_000_000L
        val repository = FocusTimerRepository(
            focusTimerDao = fakeDao,
            scope = backgroundScope,
            timeProvider = { mockRealtime },
            wallClockProvider = { mockWallClock }
        )

        repository.startSession(
            mode = FocusMode.COUNTDOWN,
            durationInMillis = 30 * 60 * 1000L,
            isLockedIn = true,
            disableEndButtonShown = true,
            isUninstallBlocked = true,
            isLauncherBlocked = true,
            blockNotifications = true,
            breaksAllowedCount = 0
        )
        testScheduler.advanceUntilIdle()

        // When realtime matches elapsed wall time, time integrity is valid
        assertTrue(repository.verifyTimeIntegrity())
        assertFalse(repository.isTimeTampered.value)

        // Advance wall clock by 2 hours ahead while realtime only advances by 1 second
        mockWallClock += 7_200_000L
        mockRealtime += 1_000L

        // Verify tamper detection
        assertFalse(repository.verifyTimeIntegrity())
        assertTrue(repository.isTimeTampered.value)
    }
}
