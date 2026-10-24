package com.personal.focus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.personal.focus.FocusApplication
import com.personal.focus.data.model.DailyAppUsageRecord
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.ui.theme.ClashDisplayFamily
import com.personal.focus.ui.theme.DarkSurface
import com.personal.focus.ui.theme.Gold
import com.personal.focus.ui.theme.Neutral0
import com.personal.focus.ui.theme.Neutral10
import com.personal.focus.ui.theme.Neutral100
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen
import com.personal.focus.ui.theme.RobotoNumbersFamily
import kotlinx.coroutines.launch
import java.util.Calendar

data class WeeklyDayStat(
    val dayLabel: String,
    val dateKey: String,
    val screentimeMinutes: Int,
    val isToday: Boolean
)

/**
 * Formats total minutes as "Xh Ym" when >= 60 minutes, or "Ym" when < 60 minutes.
 */
fun formatMinutesToHoursMins(totalMinutes: Int): String {
    if (totalMinutes <= 0) return "0m"
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    return if (hours > 0) {
        "${hours}h ${mins}m"
    } else {
        "${mins}m"
    }
}

/**
 * Formats milliseconds into "Xh Ym", "Ym", or "Zs" (if under 1 minute).
 */
fun formatMillisToHoursMins(durationMillis: Long): String {
    val totalSeconds = (durationMillis / 1000L).coerceAtLeast(0L)
    val totalMinutes = (totalSeconds / 60L).toInt()
    if (totalMinutes <= 0) {
        return if (totalSeconds > 0L) "${totalSeconds}s" else "0m"
    }
    return formatMinutesToHoursMins(totalMinutes)
}

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    remember(context) { FocusApplication.ensureInitialized(context) }
    val usageRepo = FocusStateRepository.usageStatsRepository ?: FocusApplication.instance.usageStatsRepository

    val rawTodayUsage by usageRepo.todayUsage.collectAsState()
    val totalScreenTimeMillis by usageRepo.todayTotalScreenTimeMillis.collectAsState()
    val totalTimeSavedMillis by usageRepo.totalTimeSavedMillis.collectAsState()
    val completedFocusDateKeys by usageRepo.completedFocusDateKeys.collectAsState()
    val currentStreakDays by usageRepo.currentStreakDays.collectAsState()
    val weeklyUsageSummary by usageRepo.weeklyUsageSummary.collectAsState()
    val isUsageSynced by usageRepo.isUsageSynced.collectAsState()

    var isSyncing by remember { mutableStateOf(false) }

    // Deduplicate and filter out our own package before displaying in LazyColumn
    val deduplicatedTodayUsage = remember(rawTodayUsage, context.packageName) {
        rawTodayUsage
            .filter { it.packageName != context.packageName && it.packageName != "com.personal.focus" && it.usageDurationMillis > 0L }
            .groupBy { it.packageName }
            .map { (_, entries) ->
                val first = entries.first()
                first.copy(
                    usageDurationMillis = entries.maxOf { it.usageDurationMillis },
                    lastTimeUsed = entries.maxOf { it.lastTimeUsed }
                )
            }
            .distinctBy { it.packageName }
            .sortedByDescending { it.usageDurationMillis }
    }

    // Automatically sync on screen entry and whenever returning to foreground
    LaunchedEffect(Unit) {
        try {
            usageRepo.checkAndRefreshUsageSyncFlag(context)
            usageRepo.syncUsageFromSystem(context)
        } catch (_: Throwable) {
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    try {
                        usageRepo.checkAndRefreshUsageSyncFlag(context)
                        usageRepo.syncUsageFromSystem(context)
                    } catch (_: Throwable) {
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Include even short focus sessions (1-2 mins or partial minutes) in Attention Preserved
    val totalSavedMinutes = ((totalTimeSavedMillis / 1000L) / 60L).let { mins ->
        if (mins == 0L && totalTimeSavedMillis > 0L) 1L else mins
    }
    val savedHours = totalSavedMinutes / 60L
    val savedMins = totalSavedMinutes % 60L
    val savedText = "${savedHours}h ${savedMins}m Saved"

    val screenHours = (totalScreenTimeMillis / 1000L) / 3600
    val screenMins = ((totalScreenTimeMillis / 1000L) % 3600) / 60
    val screenText = "${screenHours}h ${screenMins}m Total Screentime"

    // Current day of week for real 7-day chart & streak
    val todayCal = Calendar.getInstance()
    val currentDayOfWeek = todayCal.get(Calendar.DAY_OF_WEEK)

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val shortLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    val todayIndex = when (currentDayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

    val weekDateKeys = remember { usageRepo.getCurrentWeekDateKeys() }
    val todayMinutes = ((totalScreenTimeMillis / 1000L) / 60).toInt()

    // 100% real weekly distribution (0m for days without recorded usage)
    val weeklyData = weekDateKeys.mapIndexed { index, dateKey ->
        val dayMillis = if (index == todayIndex) {
            totalScreenTimeMillis
        } else {
            weeklyUsageSummary[dateKey] ?: 0L
        }
        WeeklyDayStat(
            dayLabel = shortLabels.getOrElse(index) { "" },
            dateKey = dateKey,
            screentimeMinutes = ((dayMillis / 1000L) / 60).toInt().coerceAtLeast(if (index == todayIndex) todayMinutes else 0),
            isToday = index == todayIndex
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral0)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        item {
            Text(
                text = "SCREENTIME & ANALYTICS",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // =========================================================================
        // 100% REAL STREAK INDICATOR (Starts at 0 on fresh install)
        // =========================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔥", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7-DAY FOCUS STREAK",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Gold.copy(alpha = 0.15f))
                                .border(1.dp, Gold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$currentStreakDays ${if (currentStreakDays == 1) "DAY" else "DAYS"}",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7-Day Dots Row (Only marked completed when user actually finishes a focus session on that date)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEachIndexed { index, day ->
                            val dateKey = weekDateKeys.getOrElse(index) { "" }
                            val isCompleted = completedFocusDateKeys.contains(dateKey)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isCompleted) Gold else Neutral10)
                                        .border(
                                            width = if (index == todayIndex) 2.dp else 0.dp,
                                            color = if (index == todayIndex) Neutral100 else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isCompleted) "✓" else day.take(1),
                                        color = if (isCompleted) Color.Black else Neutral90,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    color = if (index == todayIndex) PrimaryGreen else Neutral90,
                                    fontSize = 11.sp,
                                    fontWeight = if (index == todayIndex) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // =========================================================================
        // HERO CARD: REAL FOCUS TIME SAVED & TODAY'S SCREENTIME
        // =========================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "ATTENTION PRESERVED",
                        color = Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = savedText,
                        fontFamily = ClashDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = screenText,
                        color = Neutral90,
                        fontSize = 14.sp
                    )

                    // One-time Sync Usage Stats button: permanently hidden once synced or Usage Access is active
                    if (!isUsageSynced) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (!isSyncing) {
                                    scope.launch {
                                        isSyncing = true
                                        try {
                                            usageRepo.syncUsageFromSystem(
                                                context = context,
                                                markSyncedByUser = true
                                            )
                                        } finally {
                                            isSyncing = false
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Neutral10),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("sync_usage_stats_button")
                        ) {
                            Text(
                                text = if (isSyncing) "Syncing..." else "Sync Usage Stats",
                                color = PrimaryGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // =========================================================================
        // REAL WEEKLY USAGE BAR CHART (Fixed bottom labels + Xh Ym formatting)
        // =========================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WEEKLY SCREENTIME ACTIVITY",
                            color = Neutral90,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Goal: < 2h/day",
                            color = PrimaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val maxMinutes = (weeklyData.maxOfOrNull { it.screentimeMinutes } ?: 60).coerceAtLeast(60)
                    val maxBarHeight = 88.dp

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(144.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyData.forEach { dayStat ->
                            val heightFraction = if (dayStat.screentimeMinutes <= 0) {
                                0.05f
                            } else {
                                (dayStat.screentimeMinutes.toFloat() / maxMinutes.toFloat()).coerceIn(0.08f, 1f)
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                // Upper flexible bar + duration text container
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(
                                        text = formatMinutesToHoursMins(dayStat.screentimeMinutes),
                                        color = if (dayStat.isToday) PrimaryGreen else Neutral90,
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        softWrap = false,
                                        fontWeight = if (dayStat.isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .height(maxBarHeight * heightFraction)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(if (dayStat.isToday) PrimaryGreen else Neutral10)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Dedicated fixed-height bottom day label (M T W T F S S) that never clips
                                Box(
                                    modifier = Modifier.height(18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayStat.dayLabel,
                                        color = if (dayStat.isToday) PrimaryGreen else Neutral90,
                                        fontSize = 11.sp,
                                        fontWeight = if (dayStat.isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // =========================================================================
        // TODAY'S APP USAGE BREAKDOWN
        // =========================================================================
        item {
            Text(
                text = "TODAY'S APP BREAKDOWN",
                color = Neutral90,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (deduplicatedTodayUsage.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Usage Data Synced Yet",
                            color = Neutral100,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Grant Usage Stats permission in Settings and tap 'Sync Usage Stats' above.",
                            color = Neutral90,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            val maxUsage = (deduplicatedTodayUsage.maxOfOrNull { it.usageDurationMillis } ?: 1L).coerceAtLeast(1L)

            items(deduplicatedTodayUsage, key = { it.packageName }) { record ->
                AppUsageItem(
                    record = record,
                    maxUsageMillis = maxUsage
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun AppUsageItem(
    record: DailyAppUsageRecord,
    maxUsageMillis: Long
) {
    val durationLabel = formatMillisToHoursMins(record.usageDurationMillis)
    val progress = (record.usageDurationMillis.toFloat() / maxUsageMillis.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.appName,
                    color = Neutral100,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = durationLabel,
                    fontFamily = RobotoNumbersFamily,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryGreen,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = PrimaryGreen,
                trackColor = Neutral10
            )
        }
    }
}
