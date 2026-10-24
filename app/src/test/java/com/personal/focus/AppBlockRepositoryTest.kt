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

        assertEquals(0, repository.getDailyLimitMinutesSync("com.zhiliaoapp.musically"))

        repository.setDailyLimit("com.zhiliaoapp.musically", "TikTok", 45)
        assertEquals(45, repository.getDailyLimitMinutesSync("com.zhiliaoapp.musically"))
        assertTrue(repository.isAppBlockedSync("com.zhiliaoapp.musically"))
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
    fun `test isDistractingApp identifies updated default social apps and games and excludes Chrome WhatsApp YouTube`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)

        // Included default distracting apps
        assertTrue(repository.isDistractingApp("com.facebook.katana"))
        assertTrue(repository.isDistractingApp("com.facebook.lite"))
        assertTrue(repository.isDistractingApp("com.facebook.orca"))
        assertTrue(repository.isDistractingApp("com.facebook.mlite"))
        assertTrue(repository.isDistractingApp("com.imo.android.imoim"))
        assertTrue(repository.isDistractingApp("com.imo.android.imoimhd"))
        assertTrue(repository.isDistractingApp("com.instagram.android"))
        assertTrue(repository.isDistractingApp("com.instagram.lite"))
        assertTrue(repository.isDistractingApp("com.netflix.mediaclient"))
        assertTrue(repository.isDistractingApp("com.twitter.android"))
        assertTrue(repository.isDistractingApp("com.reddit.frontpage"))
        assertTrue(repository.isDistractingApp("com.snapchat.android"))
        assertTrue(repository.isDistractingApp("com.zhiliaoapp.musically"))
        assertTrue(repository.isDistractingApp("com.ss.android.ugc.trill"))

        // Auto-detected game packages
        assertTrue(repository.isDistractingApp("com.tencent.ig"))
        assertTrue(repository.isDistractingApp("com.supercell.clashofclans"))

        // Chrome, WhatsApp, and YouTube must NOT be in default distracting apps
        assertFalse(repository.isDistractingApp("com.android.chrome"))
        assertFalse(repository.isDistractingApp("com.whatsapp"))
        assertFalse(repository.isDistractingApp("com.google.android.youtube"))
        assertFalse(repository.isDistractingApp("com.google.android.calculator"))
    }

    @Test
    fun `test isBlockedDuringFocus defaults to blocking distracting apps unless whitelisted or removed`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)
        testScheduler.advanceUntilIdle()

        // Default distracting apps blocked by default
        assertTrue(repository.isBlockedDuringFocus("com.facebook.katana"))
        assertTrue(repository.isBlockedDuringFocus("com.instagram.android"))
        assertTrue(repository.isBlockedDuringFocus("com.imo.android.imoim"))

        // Chrome, WhatsApp, YouTube are NOT blocked by default
        assertFalse(repository.isBlockedDuringFocus("com.android.chrome"))
        assertFalse(repository.isBlockedDuringFocus("com.whatsapp"))
        assertFalse(repository.isBlockedDuringFocus("com.google.android.youtube"))

        // When whitelisted during focus, should NOT be blocked
        repository.setWhitelistedDuringFocus("com.facebook.katana", "Facebook", true)
        testScheduler.advanceUntilIdle()
        assertFalse(repository.isBlockedDuringFocus("com.facebook.katana"))
    }

    @Test
    fun `test isBlockedDuringFocus respects user unselecting or toggling off an app`() = runTest {
        val fakeDao = FakeAppBlockDao()
        val repository = AppBlockRepository(fakeDao, backgroundScope)
        testScheduler.advanceUntilIdle()

        repository.seedDefaultsIfNeeded(
            listOf(
                "com.facebook.katana" to "Facebook",
                "com.instagram.android" to "Instagram"
            )
        )
        testScheduler.advanceUntilIdle()
        assertTrue(repository.isBlockedDuringFocus("com.facebook.katana"))
        assertTrue(repository.isBlockedDuringFocus("com.instagram.android"))

        // User toggles Facebook off in Blocks tab
        repository.setAppBlocked("com.facebook.katana", "Facebook", false)
        testScheduler.advanceUntilIdle()
        assertFalse(repository.isBlockedDuringFocus("com.facebook.katana"))

        // User unselects/removes Instagram in Select Distracting Apps
        repository.deleteRuleByPackage("com.instagram.android")
        testScheduler.advanceUntilIdle()
        assertFalse(repository.isBlockedDuringFocus("com.instagram.android"))
    }
}
