package com.personal.focus.data.repository

import com.personal.focus.data.dao.AppBlockDao
import com.personal.focus.data.model.AppBlockRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Repository managing app limits, distracting apps blocklist,
 * and whitelist during focus sessions.
 */
class AppBlockRepository(
    private val appBlockDao: AppBlockDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    val allRules: StateFlow<List<AppBlockRule>> = appBlockDao.getAllRulesFlow()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val blockedRules: StateFlow<List<AppBlockRule>> = appBlockDao.getBlockedRulesFlow()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val _configuredRules = MutableStateFlow<Map<String, AppBlockRule>>(emptyMap())

    private val _blockedPackageNames = MutableStateFlow<Set<String>>(emptySet())
    val blockedPackageNames: StateFlow<Set<String>> = _blockedPackageNames.asStateFlow()

    private val _dailyLimits = MutableStateFlow<Map<String, Int>>(emptyMap())
    val dailyLimits: StateFlow<Map<String, Int>> = _dailyLimits.asStateFlow()

    private val _whitelistedPackages = MutableStateFlow<Set<String>>(emptySet())
    val whitelistedPackages: StateFlow<Set<String>> = _whitelistedPackages.asStateFlow()

    init {
        scope.launch {
            allRules.collect { rules ->
                _configuredRules.value = rules.associateBy { it.packageName }
                _blockedPackageNames.value = rules.filter { it.isBlocked }.map { it.packageName }.toSet()
                _dailyLimits.value = rules.filter { it.dailyLimitMinutes > 0 }.associate { it.packageName to it.dailyLimitMinutes }
                _whitelistedPackages.value = rules.filter { it.whitelistDuringFocus }.map { it.packageName }.toSet()
            }
        }
    }

    suspend fun setAppBlocked(
        packageName: String,
        appName: String,
        isBlocked: Boolean,
        category: String = "Distracting"
    ) {
        val existing = appBlockDao.getRule(packageName)
        val rule = existing?.copy(isBlocked = isBlocked) ?: AppBlockRule(
            packageName = packageName,
            appName = appName,
            isBlocked = isBlocked,
            category = category
        )
        _configuredRules.value = _configuredRules.value + (packageName to rule)
        if (isBlocked) {
            _blockedPackageNames.value = _blockedPackageNames.value + packageName
        } else {
            _blockedPackageNames.value = _blockedPackageNames.value - packageName
        }
        appBlockDao.insertOrUpdate(rule)
    }

    suspend fun setDailyLimit(
        packageName: String,
        appName: String,
        limitMinutes: Int,
        category: String = "Distracting"
    ) {
        val existing = appBlockDao.getRule(packageName)
        val rule = existing?.copy(dailyLimitMinutes = limitMinutes) ?: AppBlockRule(
            packageName = packageName,
            appName = appName,
            isBlocked = true,
            dailyLimitMinutes = limitMinutes,
            category = category
        )
        _configuredRules.value = _configuredRules.value + (packageName to rule)
        _dailyLimits.value = _dailyLimits.value + (packageName to limitMinutes)
        _blockedPackageNames.value = _blockedPackageNames.value + packageName
        appBlockDao.insertOrUpdate(rule)
    }

    suspend fun setWhitelistedDuringFocus(
        packageName: String,
        appName: String,
        isWhitelisted: Boolean
    ) {
        val existing = appBlockDao.getRule(packageName)
        val rule = existing?.copy(whitelistDuringFocus = isWhitelisted) ?: AppBlockRule(
            packageName = packageName,
            appName = appName,
            isBlocked = false,
            whitelistDuringFocus = isWhitelisted
        )
        _configuredRules.value = _configuredRules.value + (packageName to rule)
        if (isWhitelisted) {
            _whitelistedPackages.value = _whitelistedPackages.value + packageName
        } else {
            _whitelistedPackages.value = _whitelistedPackages.value - packageName
        }
        appBlockDao.insertOrUpdate(rule)
    }

    suspend fun insertOrUpdateRule(rule: AppBlockRule) {
        _configuredRules.value = _configuredRules.value + (rule.packageName to rule)
        if (rule.isBlocked) {
            _blockedPackageNames.value = _blockedPackageNames.value + rule.packageName
        } else {
            _blockedPackageNames.value = _blockedPackageNames.value - rule.packageName
        }
        if (rule.dailyLimitMinutes > 0) {
            _dailyLimits.value = _dailyLimits.value + (rule.packageName to rule.dailyLimitMinutes)
        } else {
            _dailyLimits.value = _dailyLimits.value - rule.packageName
        }
        if (rule.whitelistDuringFocus) {
            _whitelistedPackages.value = _whitelistedPackages.value + rule.packageName
        } else {
            _whitelistedPackages.value = _whitelistedPackages.value - rule.packageName
        }
        appBlockDao.insertOrUpdate(rule)
    }

    suspend fun deleteRule(rule: AppBlockRule) {
        _configuredRules.value = _configuredRules.value - rule.packageName
        _blockedPackageNames.value = _blockedPackageNames.value - rule.packageName
        _dailyLimits.value = _dailyLimits.value - rule.packageName
        _whitelistedPackages.value = _whitelistedPackages.value - rule.packageName
        appBlockDao.deleteRule(rule.packageName)
    }

    fun isAppBlockedSync(packageName: String): Boolean {
        return _blockedPackageNames.value.contains(packageName)
    }

    fun isDistractingApp(packageName: String): Boolean {
        if (DEFAULT_DISTRACTION_PACKAGES.contains(packageName)) return true
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

    /**
     * Determines if an app should be blocked during an active Focus Session.
     * 1. If explicitly whitelisted for focus -> allowed.
     * 2. If a rule exists in the user's configured rules -> respects rule.isBlocked.
     * 3. If no rule is configured yet (e.g. before initial setup or unconfigured apps) ->
     *    defaults to blocking distracting apps.
     */
    fun isBlockedDuringFocus(packageName: String): Boolean {
        if (isWhitelistedDuringFocusSync(packageName)) return false
        val rule = _configuredRules.value[packageName]
        if (rule != null) {
            return rule.isBlocked
        }
        return isDistractingApp(packageName)
    }

    fun isWhitelistedDuringFocusSync(packageName: String): Boolean {
        return _whitelistedPackages.value.contains(packageName)
    }

    fun getDailyLimitMinutesSync(packageName: String): Int {
        return _dailyLimits.value[packageName] ?: 0
    }

    suspend fun seedDefaultsIfNeeded(defaultDistractions: List<Pair<String, String>>) {
        val existing = appBlockDao.getAllRules()
        if (existing.isEmpty()) {
            val rules = defaultDistractions.map { (pkg, name) ->
                AppBlockRule(
                    packageName = pkg,
                    appName = name,
                    isBlocked = true,
                    dailyLimitMinutes = 30,
                    category = "Distracting"
                )
            }
            appBlockDao.insertAll(rules)
            _configuredRules.value = rules.associateBy { it.packageName }
            _blockedPackageNames.value = rules.map { it.packageName }.toSet()
        }
    }

    companion object {
        val DEFAULT_DISTRACTION_PACKAGES = setOf(
            "com.google.android.youtube",
            "com.android.chrome",
            "com.whatsapp",
            "com.instagram.android",
            "com.facebook.katana",
            "com.zhiliaoapp.musically",
            "com.twitter.android",
            "com.snapchat.android",
            "com.reddit.frontpage",
            "com.netflix.mediaclient"
        )
    }
}
