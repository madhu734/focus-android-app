package com.personal.focus.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import com.personal.focus.data.dao.DailyUsageDao
import com.personal.focus.data.model.DailyAppUsageRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Repository for screentime analytics, weekly breakdowns,
 * and time saved metrics based on UsageStatsManager and local Room storage.
 */
class UsageStatsRepository(
    private val dailyUsageDao: DailyUsageDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private fun getTodayDateKey(): String = dateFormat.format(Date())

    val todayUsage: StateFlow<List<DailyAppUsageRecord>> = dailyUsageDao.getUsageForDateFlow(getTodayDateKey())
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val _todayTotalScreenTimeMillis = MutableStateFlow(0L)
    val todayTotalScreenTimeMillis: StateFlow<Long> = _todayTotalScreenTimeMillis.asStateFlow()

    private val _totalTimeSavedMillis = MutableStateFlow(0L)
    val totalTimeSavedMillis: StateFlow<Long> = _totalTimeSavedMillis.asStateFlow()

    private val _weeklyUsageSummary = MutableStateFlow<Map<String, Long>>(emptyMap())
    val weeklyUsageSummary: StateFlow<Map<String, Long>> = _weeklyUsageSummary.asStateFlow()

    init {
        scope.launch {
            todayUsage.collect { list ->
                val total = list.sumOf { it.usageDurationMillis }
                _todayTotalScreenTimeMillis.value = total

                // Calculate time saved: baseline assumption (e.g. 4 hours average screentime) minus distracted time
                // or accumulated blocked session time
                val estimatedSaved = ((4 * 60 * 60 * 1000L) - total).coerceAtLeast(0L)
                _totalTimeSavedMillis.value = estimatedSaved
            }
        }
    }

    /**
     * Polls UsageStatsManager for today's foreground durations and updates Room.
     */
    suspend fun syncUsageFromSystem(context: Context) = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return@withContext
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        if (usageStatsList.isNullOrEmpty()) return@withContext

        val dateKey = getTodayDateKey()
        val packageManager = context.packageManager

        val records = usageStatsList
            .filter { it.totalTimeInForeground > 0 }
            .map { stats ->
                val appName = try {
                    val appInfo = packageManager.getApplicationInfo(stats.packageName, 0)
                    packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    stats.packageName
                }

                DailyAppUsageRecord(
                    dateKey = dateKey,
                    packageName = stats.packageName,
                    appName = appName,
                    usageDurationMillis = stats.totalTimeInForeground,
                    lastTimeUsed = stats.lastTimeUsed
                )
            }

        dailyUsageDao.insertAll(records)
    }

    fun getPackageTodayUsageMillis(packageName: String): Long {
        return todayUsage.value.find { it.packageName == packageName }?.usageDurationMillis ?: 0L
    }
}
