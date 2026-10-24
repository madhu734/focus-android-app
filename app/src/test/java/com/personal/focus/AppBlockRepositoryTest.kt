package com.personal.focus

import com.personal.focus.data.dao.AppBlockDao
import com.personal.focus.data.model.AppBlockRule
import com.personal.focus.data.repository.AppBlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeAppBlockDao(private val rules: MutableList<AppBlockRule> = mutableListOf()) : AppBlockDao {
    private val rulesFlow = MutableStateFlow<List<AppBlockRule>>(rules)

    override fun getAllRulesFlow(): Flow<List<AppBlockRule>> = rulesFlow

    override fun getBlockedRulesFlow(): Flow<List<AppBlockRule>> = rulesFlow

    override suspend fun getRule(packageName: String): AppBlockRule? =
        rules.firstOrNull { it.packageName == packageName }

    override suspend fun getAllRules(): List<AppBlockRule> = rules.toList()

    override suspend fun insertOrUpdate(rule: AppBlockRule) {
        rules.removeAll { it.packageName == rule.packageName }
        rules.add(rule)
        rulesFlow.value = rules.toList()
    }

    override suspend fun update(rule: AppBlockRule) = insertOrUpdate(rule)

    override suspend fun insertAll(rulesList: List<AppBlockRule>) {
        rules.addAll(rulesList)
        rulesFlow.value = rules.toList()
    }

    override suspend fun deleteRule(packageName: String) {
        rules.removeAll { it.packageName == packageName }
        rulesFlow.value = rules.toList()
    }
}

class AppBlockRepositoryTest {

    @Test
    fun `test immediate in-memory cache consistency on setAppBlocked`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)
        testScheduler.advanceUntilIdle()

        assertFalse(repository.isAppBlockedSync("com.instagram.android"))

        // Set blocked
        repository.setAppBlocked("com.instagram.android", "Instagram", true)
        // Must immediately reflect in sync query
        assertTrue(repository.isAppBlockedSync("com.instagram.android"))

        // Set unblocked
        repository.setAppBlocked("com.instagram.android", "Instagram", false)
        assertFalse(repository.isAppBlockedSync("com.instagram.android"))
    }

    @Test
    fun `test immediate in-memory cache consistency on setDailyLimit`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)
        testScheduler.advanceUntilIdle()

        assertEquals(0, repository.getDailyLimitMinutesSync("com.tiktok.android"))

        repository.setDailyLimit("com.tiktok.android", "TikTok", 45)
        assertEquals(45, repository.getDailyLimitMinutesSync("com.tiktok.android"))
        assertTrue(repository.isAppBlockedSync("com.tiktok.android"))
    }

    @Test
    fun `test immediate in-memory cache consistency on setWhitelistedDuringFocus`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)
        testScheduler.advanceUntilIdle()

        assertFalse(repository.isWhitelistedDuringFocusSync("com.slack.android"))

        repository.setWhitelistedDuringFocus("com.slack.android", "Slack", true)
        assertTrue(repository.isWhitelistedDuringFocusSync("com.slack.android"))

        repository.setWhitelistedDuringFocus("com.slack.android", "Slack", false)
        assertFalse(repository.isWhitelistedDuringFocusSync("com.slack.android"))
    }

    @Test
    fun `test isDistractingApp identifies common social and browser apps`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)

        assertTrue(repository.isDistractingApp("com.google.android.youtube"))
        assertTrue(repository.isDistractingApp("com.android.chrome"))
        assertTrue(repository.isDistractingApp("com.whatsapp"))
        assertTrue(repository.isDistractingApp("com.instagram.android"))
        assertTrue(repository.isDistractingApp("com.facebook.katana"))
        assertTrue(repository.isDistractingApp("com.zhiliaoapp.musically"))
        assertTrue(repository.isDistractingApp("com.reddit.frontpage"))
        assertFalse(repository.isDistractingApp("com.google.android.calculator"))
    }

    @Test
    fun `test isBlockedDuringFocus defaults to blocking distracting apps unless whitelisted`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)
        testScheduler.advanceUntilIdle()

        // Distracting apps blocked by default during active session
        assertTrue(repository.isBlockedDuringFocus("com.google.android.youtube"))
        assertTrue(repository.isBlockedDuringFocus("com.android.chrome"))
        assertTrue(repository.isBlockedDuringFocus("com.whatsapp"))

        // Non-distracting app is not blocked by default
        assertFalse(repository.isBlockedDuringFocus("com.example.calculator"))

        // When whitelisted during focus, should NOT be blocked
        repository.setWhitelistedDuringFocus("com.whatsapp", "WhatsApp", true)
        testScheduler.advanceUntilIdle()
        assertFalse(repository.isBlockedDuringFocus("com.whatsapp"))
    }

    @Test
    fun `test isBlockedDuringFocus respects user explicitly unchecking distracting app`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)
        testScheduler.advanceUntilIdle()

        // Seed defaults (Chrome is blocked by default)
        repository.seedDefaultsIfNeeded(
            listOf("com.android.chrome" to "Google Chrome")
        )
        testScheduler.advanceUntilIdle()
        assertTrue(repository.isBlockedDuringFocus("com.android.chrome"))

        // User toggles Chrome off in Blocks tab
        repository.setAppBlocked("com.android.chrome", "Google Chrome", false)
        testScheduler.advanceUntilIdle()

        // Chrome must now NOT be blocked during focus
        assertFalse(repository.isBlockedDuringFocus("com.android.chrome"))
    }

    @Test
    fun `test seedDefaultsIfNeeded populates rules when database is empty`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)

        val defaults = listOf(
            "com.google.android.youtube" to "YouTube",
            "com.android.chrome" to "Google Chrome",
            "com.whatsapp" to "WhatsApp"
        )
        repository.seedDefaultsIfNeeded(defaults)
        testScheduler.advanceUntilIdle()

        assertEquals(3, fakeDao.getAllRules().size)
        assertTrue(repository.isAppBlockedSync("com.google.android.youtube"))
        assertTrue(repository.isAppBlockedSync("com.android.chrome"))
        assertTrue(repository.isAppBlockedSync("com.whatsapp"))
    }
}
