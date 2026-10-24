package com.personal.focus

import com.personal.focus.data.dao.DailyUsageDao
import com.personal.focus.data.model.DailyAppUsageRecord
import com.personal.focus.data.model.FocusMode
import com.personal.focus.data.model.FocusTimerSession
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.data.repository.UsageStatsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeDailyUsageDao(
    initialRecords: List<DailyAppUsageRecord> = emptyList()
) : DailyUsageDao {
    private val records = initialRecords.toMutableList()
    private val flow = MutableStateFlow(records.toList())

    override fun getUsageForDateFlow(dateKey: String): Flow<List<DailyAppUsageRecord>> =
        flow.map { list -> list.filter { it.dateKey == dateKey } }

    override suspend fun getUsageForDate(dateKey: String): List<DailyAppUsageRecord> =
        records.filter { it.dateKey == dateKey }

    override fun getWeeklyUsageFlow(dateKeys: List<String>): Flow<List<DailyAppUsageRecord>> =
        flow.map { list -> list.filter { it.dateKey in dateKeys } }

    override suspend fun getUsageForDates(dateKeys: List<String>): List<DailyAppUsageRecord> =
        records.filter { it.dateKey in dateKeys }

    override fun getTotalUsageForDateFlow(dateKey: String): Flow<Long?> =
        flow.map { list -> list.filter { it.dateKey == dateKey }.sumOf { it.usageDurationMillis } }

    override suspend fun deleteUsageForDate(dateKey: String) {
        records.removeAll { it.dateKey == dateKey }
        flow.value = records.toList()
    }

    override suspend fun deletePackageUsage(packageName: String) {
        records.removeAll { it.packageName == packageName }
        flow.value = records.toList()
    }

    override suspend fun insertOrUpdate(record: DailyAppUsageRecord) {
        records.add(record)
        flow.value = records.toList()
    }

    override suspend fun insertAll(recordsList: List<DailyAppUsageRecord>) {
        records.addAll(recordsList)
        flow.value = records.toList()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class UsageStatsRepositoryTest {

    @Test
    fun `test todayUsage deduplicates duplicate package keys and excludes com_personal_focus`() = runTest {
        val fakeUsageDao = FakeDailyUsageDao()
        val fakeTimerDao = FakeFocusTimerDao()
        val repoScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repository = UsageStatsRepository(fakeUsageDao, repoScope, fakeTimerDao)
        val todayKey = repository.getTodayDateKey()

        // Insert duplicate entries including com.personal.focus
        fakeUsageDao.insertAll(
            listOf(
                DailyAppUsageRecord(id = 1, dateKey = todayKey, packageName = "com.personal.focus", appName = "Focus", usageDurationMillis = 120_000L, lastTimeUsed = 1000L),
                DailyAppUsageRecord(id = 2, dateKey = todayKey, packageName = "com.personal.focus", appName = "Focus", usageDurationMillis = 180_000L, lastTimeUsed = 2000L),
                DailyAppUsageRecord(id = 3, dateKey = todayKey, packageName = "com.instagram.android", appName = "Instagram", usageDurationMillis = 300_000L, lastTimeUsed = 1500L),
                DailyAppUsageRecord(id = 4, dateKey = todayKey, packageName = "com.instagram.android", appName = "Instagram", usageDurationMillis = 600_000L, lastTimeUsed = 2500L),
                DailyAppUsageRecord(id = 5, dateKey = todayKey, packageName = "com.facebook.katana", appName = "Facebook", usageDurationMillis = 240_000L, lastTimeUsed = 1800L)
            )
        )
        testScheduler.advanceUntilIdle()

        val list = repository.todayUsage.value
        val packageKeys = list.map { it.packageName }

        // 1. Self package "com.personal.focus" must be filtered out
        assertFalse(packageKeys.contains("com.personal.focus"))

        // 2. Keys must be strictly unique (no duplicates for LazyColumn)
        assertEquals(packageKeys.distinct().size, packageKeys.size)
        assertEquals(2, list.size)
        assertEquals("com.instagram.android", list[0].packageName)
        assertEquals(600_000L, list[0].usageDurationMillis)
    }

    @Test
    fun `test fresh install starts with 0 streak and 0 time saved and updates on real completed session`() = runTest {
        val fakeUsageDao = FakeDailyUsageDao()
        val fakeTimerDao = FakeFocusTimerDao()
        val repoScope = TestScope(UnconfinedTestDispatcher(testScheduler))
        val repository = UsageStatsRepository(fakeUsageDao, repoScope, fakeTimerDao)
        testScheduler.advanceUntilIdle()

        // Fresh install -> 0 streak, 0 time saved, empty completed dates
        assertEquals(0, repository.currentStreakDays.value)
        assertEquals(0L, repository.totalTimeSavedMillis.value)
        assertTrue(repository.completedFocusDateKeys.value.isEmpty())

        // Complete a real 25-minute focus session today
        val now = System.currentTimeMillis()
        fakeTimerDao.insertSession(
            FocusTimerSession(
                sessionId = 1L,
                startTime = now,
                durationInMillis = 25 * 60 * 1000L,
                mode = FocusMode.COUNTDOWN,
                isLockedIn = false,
                disableEndButtonShown = false,
                isUninstallBlocked = true,
                isLauncherBlocked = false,
                blockNotifications = true,
                breaksAllowedCount = 1,
                breakDurationInMillis = 5 * 60 * 1000L,
                longBreakDurationInMillis = 15 * 60 * 1000L,
                sessionStatus = SessionStatus.COMPLETED,
                elapsedRealtimeStart = 1000L,
                totalFocusTimeSpentMillis = 25 * 60 * 1000L
            )
        )
        testScheduler.advanceUntilIdle()

        assertEquals(25 * 60 * 1000L, repository.totalTimeSavedMillis.value)
        assertEquals(1, repository.currentStreakDays.value)
        assertTrue(repository.completedFocusDateKeys.value.contains(repository.getTodayDateKey()))

        // Early cancelled session after 2 minutes of focus also counts toward Attention Preserved
        fakeTimerDao.insertSession(
            FocusTimerSession(
                sessionId = 2L,
                startTime = now,
                durationInMillis = 25 * 60 * 1000L,
                mode = FocusMode.COUNTDOWN,
                isLockedIn = false,
                disableEndButtonShown = false,
                isUninstallBlocked = true,
                isLauncherBlocked = false,
                blockNotifications = true,
                breaksAllowedCount = 1,
                breakDurationInMillis = 5 * 60 * 1000L,
                longBreakDurationInMillis = 15 * 60 * 1000L,
                sessionStatus = SessionStatus.CANCELLED,
                elapsedRealtimeStart = 2000L,
                totalFocusTimeSpentMillis = 2 * 60 * 1000L
            )
        )
        testScheduler.advanceUntilIdle()

        assertEquals(2 * 60 * 1000L, repository.totalTimeSavedMillis.value)
    }

    @Test
    fun `test flexible give up phrase verification ignores case punctuation and extra spaces`() {
        assertTrue(com.personal.focus.ui.screens.isGiveUpVerificationPhraseMatched("sorry syllabus sesh hoynai"))
        assertTrue(com.personal.focus.ui.screens.isGiveUpVerificationPhraseMatched("  SORRY,  Syllabus   sesh hoynai!!?! "))
        assertTrue(com.personal.focus.ui.screens.isGiveUpVerificationPhraseMatched("Sorry. Syllabus, sesh 'hoynai'..."))
        assertFalse(com.personal.focus.ui.screens.isGiveUpVerificationPhraseMatched("syllabus sesh hoyeche"))
        assertFalse(com.personal.focus.ui.screens.isGiveUpVerificationPhraseMatched(""))
    }

    @Test
    fun `test Xh Ym formatting for weekly bar chart and app usage list`() {
        assertEquals("22h 22m", com.personal.focus.ui.screens.formatMinutesToHoursMins(1342))
        assertEquals("11h 24m", com.personal.focus.ui.screens.formatMinutesToHoursMins(684))
        assertEquals("3h 44m", com.personal.focus.ui.screens.formatMinutesToHoursMins(224))
        assertEquals("45m", com.personal.focus.ui.screens.formatMinutesToHoursMins(45))
        assertEquals("0m", com.personal.focus.ui.screens.formatMinutesToHoursMins(0))
        assertEquals("3h 44m", com.personal.focus.ui.screens.formatMillisToHoursMins(224L * 60_000L))
    }
}
