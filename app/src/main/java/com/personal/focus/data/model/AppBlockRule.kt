package com.personal.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * App block and usage limit rule entity.
 */
@Entity(tableName = "app_block_rules")
data class AppBlockRule(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val dailyLimitMinutes: Int = 0,        // 0 = no limit, > 0 = daily screentime quota in minutes
    val category: String = "Distracting",  // e.g. "Social", "Entertainment", "Games"
    val whitelistDuringFocus: Boolean = false
)
