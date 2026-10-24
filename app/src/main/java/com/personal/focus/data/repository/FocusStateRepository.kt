package com.personal.focus.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.personal.focus.data.FocusDatabase
import com.personal.focus.data.model.AppBlockRule
import com.personal.focus.data.model.FocusTimerSession
import com.personal.focus.data.model.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SelectableAppInfo(
    val appName: String,
    val packageName: String,
    val isGame: Boolean = false
)

/**
 * Shared Reactive State Singleton (FocusStateRepository) aligned with CLEAN_ARCHITECTURE.md.
 *
 * Maintains in-memory StateFlow<FocusTimerSession?> and StateFlow<Set<String>> (blocked package names)
 * synced with Room DB so both MyMonitorService and MyMonitorInAppBlockService know instantaneously
 * whether a focus session is active and which packages must be intercepted.
 */
object FocusStateRepository {

    val DEFAULT_DISTRACTION_ENTRIES: List<Pair<String, String>> = listOf(
        "com.facebook.katana" to "Facebook",
        "com.facebook.lite" to "Facebook Lite",
        "com.facebook.orca" to "Messenger",
        "com.facebook.mlite" to "Messenger Lite",
        "com.imo.android.imoim" to "imo",
        "com.imo.android.imoimhd" to "imo HD",
        "com.instagram.android" to "Instagram",
        "com.instagram.lite" to "Instagram Lite",
        "com.netflix.mediaclient" to "Netflix",
        "com.twitter.android" to "Twitter / X",
        "com.reddit.frontpage" to "Reddit",
        "com.snapchat.android" to "Snapchat",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.ss.android.ugc.trill" to "TikTok"
    )

    val DEFAULT_BLOCKED_PACKAGES: Set<String> = DEFAULT_DISTRACTION_ENTRIES.map { it.first }.toSet()

    private val REMOVED_FROM_DEFAULTS: Set<String> = setOf(
        "com.android.chrome",
        "com.whatsapp",
        "com.google.android.youtube"
    )

    private val CORE_SYSTEM_PACKAGES_TO_EXCLUDE: Set<String> = setOf(
        "com.android.settings",
        "com.android.systemui",
        "com.android.dialer",
        "com.android.phone",
        "com.android.contacts",
        "com.android.mms",
        "com.android.deskclock",
        "com.android.calculator2",
        "com.android.calendar",
        "com.android.camera",
        "com.android.camera2",
        "com.android.documentsui",
        "com.android.stk",
        "com.android.vending",
        "com.android.packageinstaller",
        "com.android.permissioncontroller",
        "com.android.traceur",
        "com.android.development",
        "com.google.android.dialer",
        "com.google.android.contacts",
        "com.google.android.apps.messaging",
        "com.google.android.deskclock",
        "com.google.android.calculator",
        "com.google.android.calendar",
        "com.google.android.GoogleCamera",
        "com.google.android.documentsui",
        "com.google.android.gms",
        "com.google.android.gsf",
        "com.google.android.packageinstaller",
        "com.google.android.permissioncontroller",
        "com.google.android.apps.nexuslauncher",
        "com.android.launcher",
        "com.android.launcher3"
    )

    private val USER_FACING_OPTIONAL_PACKAGES: Set<String> = setOf(
        "com.google.android.youtube",
        "com.android.chrome",
        "com.whatsapp",
        "com.google.android.apps.youtube.music",
        "com.google.android.videos",
        "com.spotify.music",
        "com.discord",
        "org.telegram.messenger",
        "tv.twitch.android.app",
        "com.pinterest"
    )

    private const val PREFS_NAME = "focus_distracting_apps_prefs"
    private const val KEY_MIGRATED_V2 = "default_list_v2_migrated"
    private const val KEY_SEEDED_PACKAGES = "seeded_packages_v2"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _activeSession = MutableStateFlow<FocusTimerSession?>(null)
    val activeSession: StateFlow<FocusTimerSession?> = _activeSession.asStateFlow()

    private val _isFocusActive = MutableStateFlow(false)
    val isFocusActive: StateFlow<Boolean> = _isFocusActive.asStateFlow()

    private val _isStrictActive = MutableStateFlow(false)
    val isStrictActive: StateFlow<Boolean> = _isStrictActive.asStateFlow()

    private val _remainingTimeMillis = MutableStateFlow(0L)
    val remainingTimeMillis: StateFlow<Long> = _remainingTimeMillis.asStateFlow()

    // Pre-populated with default blocked packages so blocking works out-of-the-box immediately
    private val _blockedPackageNames = MutableStateFlow<Set<String>>(DEFAULT_BLOCKED_PACKAGES)
    val blockedPackageNames: StateFlow<Set<String>> = _blockedPackageNames.asStateFlow()

    private val _whitelistedPackages = MutableStateFlow<Set<String>>(emptySet())
    val whitelistedPackages: StateFlow<Set<String>> = _whitelistedPackages.asStateFlow()

    @Volatile
    private var isInitialized = false

    @Volatile
    private var hasSyncedInstalledApps = false

    @Volatile
    var database: FocusDatabase? = null
        private set

    @Volatile
    var focusTimerRepository: FocusTimerRepository? = null
        private set

    @Volatile
    var appBlockRepository: AppBlockRepository? = null
        private set

    @Volatile
    var youTubeStudyRepository: YouTubeStudyRepository? = null
        private set

    @Volatile
    var usageStatsRepository: UsageStatsRepository? = null
        private set

    /**
     * Thread-safe initialization from any Context (Application, Activity, Service, or Receiver).
     */
    fun initialize(
        context: Context,
        existingTimerRepo: FocusTimerRepository? = null,
        existingBlockRepo: AppBlockRepository? = null,
        existingYouTubeRepo: YouTubeStudyRepository? = null,
        existingUsageRepo: UsageStatsRepository? = null
    ) {
        if (isInitialized && focusTimerRepository != null && appBlockRepository != null) {
            return
        }
        synchronized(this) {
            if (isInitialized && focusTimerRepository != null && appBlockRepository != null) {
                return
            }
            val appContext = context.applicationContext ?: context
            val db = FocusDatabase.getInstance(appContext)
            database = db

            val timerRepo = existingTimerRepo ?: focusTimerRepository ?: FocusTimerRepository(db.focusTimerDao(), scope)
            val blockRepo = existingBlockRepo ?: appBlockRepository ?: AppBlockRepository(db.appBlockDao(), scope)
            val ytRepo = existingYouTubeRepo ?: youTubeStudyRepository ?: YouTubeStudyRepository(db.studyChannelDao(), scope)
            val usageRepo = existingUsageRepo ?: usageStatsRepository ?: UsageStatsRepository(
                dailyUsageDao = db.dailyUsageDao(),
                scope = scope,
                focusTimerDao = db.focusTimerDao()
            )

            focusTimerRepository = timerRepo
            appBlockRepository = blockRepo
            youTubeStudyRepository = ytRepo
            usageStatsRepository = usageRepo
            isInitialized = true

            bindRepositoryFlows(appContext, timerRepo, blockRepo)
        }
    }

    private fun bindRepositoryFlows(
        appContext: Context,
        timerRepo: FocusTimerRepository,
        blockRepo: AppBlockRepository
    ) {
        scope.launch {
            timerRepo.activeSession.collect { session ->
                _activeSession.value = session
                val active = session != null &&
                        session.sessionStatus == SessionStatus.ACTIVE &&
                        !session.isInBreak
                _isFocusActive.value = active
                _isStrictActive.value = session?.isLockedIn == true &&
                        (session.sessionStatus == SessionStatus.ACTIVE || session.sessionStatus == SessionStatus.PAUSED)
            }
        }

        scope.launch {
            timerRepo.remainingTimeMillis.collect { remaining ->
                _remainingTimeMillis.value = remaining
            }
        }

        scope.launch {
            blockRepo.allRules.collect { rules ->
                if (rules.isEmpty() && !hasSyncedInstalledApps) {
                    _blockedPackageNames.value = DEFAULT_BLOCKED_PACKAGES
                    _whitelistedPackages.value = emptySet()
                } else {
                    _blockedPackageNames.value = rules.filter { it.isBlocked }.map { it.packageName }.toSet()
                    _whitelistedPackages.value = rules.filter { it.whitelistDuringFocus }.map { it.packageName }.toSet()
                }
            }
        }

        scope.launch {
            try {
                syncInstalledAppsAndDefaults(appContext)
            } catch (_: Throwable) {
            }
        }
    }

    /**
     * Checks if a package is actually installed on the user's phone via PackageManager.
     */
    fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getApplicationInfo(packageName, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Detects whether an installed ApplicationInfo is a Game using CATEGORY_GAME, FLAG_IS_GAME,
     * and known popular game package names/prefixes.
     */
    fun isGameApp(appInfo: ApplicationInfo): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appInfo.category == ApplicationInfo.CATEGORY_GAME) {
            return true
        }
        @Suppress("DEPRECATION")
        if ((appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0) {
            return true
        }
        return AppBlockRepository.isKnownGamePackage(appInfo.packageName)
    }

    /**
     * Synchronizes the Room DB AppBlockRule table with actually installed packages:
     * 1. Removes Chrome, WhatsApp, and YouTube if previously seeded by default.
     * 2. Removes any rule whose package is NOT installed on the phone (crucial requirement).
     * 3. Auto-adds installed apps from DEFAULT_DISTRACTION_ENTRIES and auto-detected installed Games
     *    if they have not been seeded yet.
     */
    suspend fun syncInstalledAppsAndDefaults(context: Context) {
        val appContext = context.applicationContext ?: context
        val blockRepo = appBlockRepository ?: return
        val pm = appContext.packageManager
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val seededHistory = prefs.getStringSet(KEY_SEEDED_PACKAGES, emptySet())?.toMutableSet() ?: mutableSetOf()
        val isMigratedV2 = prefs.getBoolean(KEY_MIGRATED_V2, false)

        val currentRules = database?.appBlockDao()?.getAllRules().orEmpty()

        // 1. One-time cleanup of old defaults (Chrome, WhatsApp, YouTube)
        if (!isMigratedV2) {
            for (removedPkg in REMOVED_FROM_DEFAULTS) {
                blockRepo.deleteRuleByPackage(removedPkg)
            }
        }

        // 2. Crucial: Remove any rule from DB that is NOT actually installed on the device
        for (rule in currentRules) {
            if (!isPackageInstalled(pm, rule.packageName)) {
                blockRepo.deleteRuleByPackage(rule.packageName)
            }
        }

        val remainingRulesMap = database?.appBlockDao()?.getAllRules().orEmpty().associateBy { it.packageName }

        // 3. Seed installed default distracting apps (only if installed on device & not previously seeded)
        for ((pkg, fallbackName) in DEFAULT_DISTRACTION_ENTRIES) {
            if (isPackageInstalled(pm, pkg) && !seededHistory.contains(pkg)) {
                if (!remainingRulesMap.containsKey(pkg)) {
                    val label = resolveAppLabel(pm, pkg, fallbackName)
                    blockRepo.insertOrUpdateRule(
                        AppBlockRule(
                            packageName = pkg,
                            appName = label,
                            isBlocked = true,
                            dailyLimitMinutes = 30,
                            category = "Distracting"
                        )
                    )
                }
                seededHistory.add(pkg)
            }
        }

        // 4. Auto-detect installed gaming apps via PackageManager and add as selected
        val installedApps = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getInstalledApplications(0)
            }
        } catch (_: Throwable) {
            emptyList()
        }

        for (appInfo in installedApps) {
            val pkg = appInfo.packageName
            if (pkg == appContext.packageName) continue
            if (CORE_SYSTEM_PACKAGES_TO_EXCLUDE.contains(pkg)) continue

            if (isGameApp(appInfo) && !seededHistory.contains(pkg)) {
                if (!remainingRulesMap.containsKey(pkg)) {
                    val label = try {
                        appInfo.loadLabel(pm).toString().ifBlank { pkg }
                    } catch (_: Throwable) {
                        pkg
                    }
                    blockRepo.insertOrUpdateRule(
                        AppBlockRule(
                            packageName = pkg,
                            appName = label,
                            isBlocked = true,
                            dailyLimitMinutes = 30,
                            category = "Game"
                        )
                    )
                }
                seededHistory.add(pkg)
            }
        }

        hasSyncedInstalledApps = true
        prefs.edit()
            .putBoolean(KEY_MIGRATED_V2, true)
            .putStringSet(KEY_SEEDED_PACKAGES, seededHistory)
            .apply()
    }

    /**
     * Returns all installed user apps (excluding core system apps) for the
     * "Select Distracting Apps" checkbox picker.
     */
    fun querySelectableUserApps(
        context: Context,
        currentlySelectedPackages: Set<String> = emptySet()
    ): List<SelectableAppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(intent, 0)
            }
        } catch (_: Throwable) {
            emptyList()
        }

        return resolveInfos
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                val pkg = activityInfo.packageName
                if (pkg == context.packageName) return@mapNotNull null
                if (CORE_SYSTEM_PACKAGES_TO_EXCLUDE.contains(pkg)) return@mapNotNull null
                if (isLauncherPackageName(pkg)) return@mapNotNull null

                val appInfo = activityInfo.applicationInfo ?: return@mapNotNull null
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                val isGame = isGameApp(appInfo)
                val isDefaultDistraction = DEFAULT_BLOCKED_PACKAGES.contains(pkg)
                val isUserFacingOptional = USER_FACING_OPTIONAL_PACKAGES.contains(pkg)
                val isAlreadySelected = currentlySelectedPackages.contains(pkg)

                // Include user-installed apps, updated user apps, games, social/media apps, or already selected apps
                val shouldInclude = !isSystem || isUpdatedSystem || isGame || isDefaultDistraction || isUserFacingOptional || isAlreadySelected
                if (!shouldInclude) return@mapNotNull null

                val label = try {
                    resolveInfo.loadLabel(pm).toString().ifBlank { pkg }
                } catch (_: Throwable) {
                    pkg
                }
                SelectableAppInfo(
                    appName = label,
                    packageName = pkg,
                    isGame = isGame
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
    }

    private fun isLauncherPackageName(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower.contains("launcher") || lower.contains("trebuchet")
    }

    private fun resolveAppLabel(pm: PackageManager, packageName: String, fallback: String): String {
        return try {
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getApplicationInfo(packageName, 0)
            }
            pm.getApplicationLabel(appInfo).toString().ifBlank { fallback }
        } catch (_: Throwable) {
            fallback
        }
    }

    fun isSessionActiveNow(): Boolean {
        val session = _activeSession.value ?: focusTimerRepository?.activeSession?.value
        return session != null &&
                session.sessionStatus == SessionStatus.ACTIVE &&
                !session.isInBreak
    }

    fun isPackageBlockedDuringFocus(packageName: String): Boolean {
        if (_whitelistedPackages.value.contains(packageName)) return false
        val blockRepo = appBlockRepository
        if (blockRepo != null) {
            if (blockRepo.isWhitelistedDuringFocusSync(packageName)) return false
            if (blockRepo.isBlockedDuringFocus(packageName)) return true
            if (hasSyncedInstalledApps) return false
        }
        return _blockedPackageNames.value.contains(packageName)
    }

    fun shouldBlockAppDuringActiveSession(packageName: String): Boolean {
        if (!isSessionActiveNow()) return false
        return isPackageBlockedDuringFocus(packageName)
    }
}
