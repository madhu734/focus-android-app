package com.personal.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Daily aggregated app usage metrics for screentime analytics.
 */
@Entity(
    tableName = "daily_app_usage"
)
data class DailyAppUsageRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String,                     // YYYY-MM-DD
    val packageName: String,
    val appName: String,
    val usageDurationMillis: Long,           // Screentime in milliseconds
    val lastTimeUsed: Long
)
