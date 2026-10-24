package com.personal.focus.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.personal.focus.data.dao.DailyUsageDao
import com.personal.focus.data.dao.FocusTimerDao
import com.personal.focus.data.model.DailyAppUsageRecord
import com.personal.focus.data.model.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Repository for 100% real local screentime analytics, weekly breakdowns,
 * focus streaks, and time saved metrics based on UsageStatsManager and Room DB.
 */
class UsageStatsRepository(
    private val dailyUsageDao: DailyUsageDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val focusTimerDao: FocusTimerDao? = null
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayDateKey(): String = formatDateKey(Date())

    fun formatDateKey(date: Date): String = synchronized(dateFormat) {
        dateFormat.format(date)
    }

    /**
     * Today's app usage list, strictly filtered to exclude our own package ("com.personal.focus"),
     * grouped and deduplicated by packageName so LazyColumn never encounters duplicate keys.
     */
    private val _todayUsage = MutableStateFlow<List<DailyAppUsageRecord>>(emptyList())
    val todayUsage: StateFlow<List<DailyAppUsageRecord>> = _todayUsage.asStateFlow()

    private val _todayTotalScreenTimeMillis = MutableStateFlow(0L)
    val todayTotalScreenTimeMillis: StateFlow<Long> = _todayTotalScreenTimeMillis.asStateFlow()

    // Real focus time saved from completed focus sessions (starts at 0L on fresh install)
    private val _totalTimeSavedMillis = MutableStateFlow(0L)
    val totalTimeSavedMillis: StateFlow<Long> = _totalTimeSavedMillis.asStateFlow()

    // Real set of dateKeys (yyyy-MM-dd) that have at least one completed focus session
    private val _completedFocusDateKeys = MutableStateFlow<Set<String>>(emptySet())
    val completedFocusDateKeys: StateFlow<Set<String>> = _completedFocusDateKeys.asStateFlow()

    // Real consecutive focus streak count (starts at 0 on fresh install)
    private val _currentStreakDays = MutableStateFlow(0)
    val currentStreakDays: StateFlow<Int> = _currentStreakDays.asStateFlow()

    // Real weekly usage map: dateKey (yyyy-MM-dd) -> total screentime millis for that day
    private val _weeklyUsageSummary = MutableStateFlow<Map<String, Long>>(emptyMap())
    val weeklyUsageSummary: StateFlow<Map<String, Long>> = _weeklyUsageSummary.asStateFlow()

    // Persistent flag indicating whether initial usage sync has been performed or Usage Access is active
    private val _isUsageSynced = MutableStateFlow(false)
    val isUsageSynced: StateFlow<Boolean> = _isUsageSynced.asStateFlow()

    init {
        scope.launch {
            dailyUsageDao.getUsageForDateFlow(getTodayDateKey()).collect { rawList ->
                val deduplicated = deduplicateUsageRecords(rawList)
                _todayUsage.value = deduplicated
                val total = deduplicated.sumOf { it.usageDurationMillis }
                _todayTotalScreenTimeMillis.value = total
                val todayKey = getTodayDateKey()
                _weeklyUsageSummary.value = _weeklyUsageSummary.value + (todayKey to total)
            }
        }

        // Observe FocusTimerSessions from Room DB for Time Saved & Streak
        // Includes both COMPLETED and CANCELLED (early exit) sessions that have actual elapsed focus time > 0
        if (focusTimerDao != null) {
            scope.launch {
                focusTimerDao.getAllSessionsFlow().collect { sessions ->
                    val recordedFocusSessions = sessions.filter {
                        (it.sessionStatus == SessionStatus.COMPLETED || it.sessionStatus == SessionStatus.CANCELLED) &&
                                it.totalFocusTimeSpentMillis > 0L
                    }
                    // Time saved = actual focus duration spent by user across completed and early-stopped sessions
                    val totalSaved = recordedFocusSessions.sumOf { it.totalFocusTimeSpentMillis }
                    _totalTimeSavedMillis.value = totalSaved

                    val completedDates = recordedFocusSessions
                        .map { formatDateKey(Date(it.startTime)) }
                        .toSet()
                    _completedFocusDateKeys.value = completedDates
                    _currentStreakDays.value = calculateConsecutiveStreak(completedDates)
                }
            }
        }

        // Load existing weekly usage from Room DB on init
        scope.launch {
            refreshWeeklySummaryFromDb()
        }
    }

    fun deduplicateUsageRecords(rawList: List<DailyAppUsageRecord>): List<DailyAppUsageRecord> {
        return rawList
            .filter { it.packageName != SELF_PACKAGE && it.usageDurationMillis > 0L && !isExcludedSystemPackage(it.packageName) }
            .groupBy { it.packageName }
            .map { (_, entries) ->
                val latest = entries.maxByOrNull { it.lastTimeUsed } ?: entries.first()
                latest.copy(
                    usageDurationMillis = entries.maxOf { it.usageDurationMillis },
                    lastTimeUsed = entries.maxOf { it.lastTimeUsed }
                )
            }
            .distinctBy { it.packageName }
            .sortedByDescending { it.usageDurationMillis }
    }

    private fun calculateConsecutiveStreak(completedDates: Set<String>): Int {
        if (completedDates.isEmpty()) return 0

        val cal = Calendar.getInstance()
        val todayKey = formatDateKey(cal.time)
        if (!completedDates.contains(todayKey)) {
            // Check if yesterday was completed; if neither today nor yesterday has a completed session, streak is 0
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayKey = formatDateKey(cal.time)
            if (!completedDates.contains(yesterdayKey)) {
                return 0
            }
        }

        var streak = 0
        while (true) {
            val key = formatDateKey(cal.time)
            if (completedDates.contains(key)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    private suspend fun refreshWeeklySummaryFromDb() {
        try {
            val weekKeys = getCurrentWeekDateKeys()
            val records = dailyUsageDao.getUsageForDates(weekKeys)
                .filter { it.packageName != SELF_PACKAGE && !isExcludedSystemPackage(it.packageName) }

            val summary = mutableMapOf<String, Long>()
            for (key in weekKeys) {
                val dayRecords = records
                    .filter { it.dateKey == key }
                    .groupBy { it.packageName }
                    .map { (_, entries) -> entries.maxOf { it.usageDurationMillis } }
                summary[key] = dayRecords.sum()
            }
            _weeklyUsageSummary.value = summary
        } catch (_: Throwable) {
        }
    }

    /**
     * Returns the 7 dateKeys (yyyy-MM-dd) for Monday..Sunday of the current week.
     */
    fun getCurrentWeekDateKeys(): List<String> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

        return (0 until 7).map {
            val key = formatDateKey(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
            key
        }
    }

    fun hasUsageAccessPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: Throwable) {
            false
        }
    }

    fun checkAndRefreshUsageSyncFlag(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_USAGE_SYNC, Context.MODE_PRIVATE)
        val alreadySynced = prefs.getBoolean(KEY_IS_USAGE_SYNCED, false)
        val hasPerm = hasUsageAccessPermission(context)
        val effective = alreadySynced || hasPerm
        if (effective && !alreadySynced) {
            prefs.edit().putBoolean(KEY_IS_USAGE_SYNCED, true).apply()
        }
        _isUsageSynced.value = effective
        return effective
    }

    fun markUsageSynced(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_USAGE_SYNC, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_USAGE_SYNCED, true).apply()
        _isUsageSynced.value = true
    }

    /**
     * Polls UsageStatsManager for the current week's daily foreground durations,
     * groups duplicate entries by packageName, excludes com.personal.focus, and updates Room.
     */
    suspend fun syncUsageFromSystem(
        context: Context,
        markSyncedByUser: Boolean = false
    ) = withContext(Dispatchers.IO) {
        if (markSyncedByUser || hasUsageAccessPermission(context)) {
            markUsageSynced(context)
        } else {
            checkAndRefreshUsageSyncFlag(context)
        }
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return@withContext
        val packageManager = context.packageManager
        val selfPkg = context.packageName

        // Remove any lingering records for our own package
        try {
            dailyUsageDao.deletePackageUsage(selfPkg)
            dailyUsageDao.deletePackageUsage(SELF_PACKAGE)
        } catch (_: Throwable) {
        }

        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

        val updatedWeeklyMap = mutableMapOf<String, Long>()

        for (dayIndex in 0 until 7) {
            val dayStart = cal.timeInMillis
            val dateKey = formatDateKey(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = cal.timeInMillis.coerceAtMost(now)

            if (dayStart > now) {
                // Future day in current week -> 0ms
                dailyUsageDao.deleteUsageForDate(dateKey)
                updatedWeeklyMap[dateKey] = 0L
                continue
            }

            val usageStatsList = try {
                usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    dayStart,
                    dayEnd
                )
            } catch (_: Throwable) {
                null
            }

            if (usageStatsList.isNullOrEmpty()) {
                updatedWeeklyMap[dateKey] = 0L
                continue
            }

            // Group by packageName and aggregate totalTimeInForeground to avoid duplicate keys
            val aggregatedRecords = usageStatsList
                .filter { stats ->
                    val pkg = stats.packageName
                    !pkg.isNullOrBlank() &&
                            pkg != selfPkg &&
                            pkg != SELF_PACKAGE &&
                            !isExcludedSystemPackage(pkg) &&
                            stats.totalTimeInForeground > 0L &&
                            stats.lastTimeUsed >= dayStart
                }
                .groupBy { it.packageName }
                .map { (pkg, entries) ->
                    val totalForegroundMs = entries.sumOf { it.totalTimeInForeground }
                    val lastUsed = entries.maxOf { it.lastTimeUsed }
                    val appName = resolveAppName(packageManager, pkg)

                    DailyAppUsageRecord(
                        dateKey = dateKey,
                        packageName = pkg,
                        appName = appName,
                        usageDurationMillis = totalForegroundMs,
                        lastTimeUsed = lastUsed
                    )
                }
                .filter { it.usageDurationMillis > 0L }
                .distinctBy { it.packageName }
                .sortedByDescending { it.usageDurationMillis }

            dailyUsageDao.deleteUsageForDate(dateKey)
            if (aggregatedRecords.isNotEmpty()) {
                dailyUsageDao.insertAll(aggregatedRecords)
            }
            updatedWeeklyMap[dateKey] = aggregatedRecords.sumOf { it.usageDurationMillis }
        }

        _weeklyUsageSummary.value = updatedWeeklyMap
    }

    private fun resolveAppName(packageManager: PackageManager, packageName: String): String {
        return try {
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            packageManager.getApplicationLabel(appInfo).toString().ifBlank { packageName }
        } catch (_: Throwable) {
            packageName
        }
    }

    private fun isExcludedSystemPackage(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower == "com.android.systemui" ||
                lower == "android" ||
                lower.contains("launcher") ||
                lower.contains("trebuchet") ||
                lower.contains("inputmethod") ||
                lower.contains("keyboard")
    }

    fun getPackageTodayUsageMillis(packageName: String): Long {
        if (packageName == SELF_PACKAGE) return 0L
        return todayUsage.value.find { it.packageName == packageName }?.usageDurationMillis ?: 0L
    }

    companion object {
        const val SELF_PACKAGE = "com.personal.focus"
        private const val PREFS_USAGE_SYNC = "focus_usage_sync_prefs"
        private const val KEY_IS_USAGE_SYNCED = "is_usage_synced"
    }
}
