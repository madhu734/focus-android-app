package com.personal.focus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.personal.focus.data.model.DailyAppUsageRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyUsageDao {

    @Query("SELECT * FROM daily_app_usage WHERE dateKey = :dateKey ORDER BY usageDurationMillis DESC")
    fun getUsageForDateFlow(dateKey: String): Flow<List<DailyAppUsageRecord>>

    @Query("SELECT * FROM daily_app_usage WHERE dateKey = :dateKey ORDER BY usageDurationMillis DESC")
    suspend fun getUsageForDate(dateKey: String): List<DailyAppUsageRecord>

    @Query("SELECT * FROM daily_app_usage WHERE dateKey IN (:dateKeys) ORDER BY dateKey DESC")
    fun getWeeklyUsageFlow(dateKeys: List<String>): Flow<List<DailyAppUsageRecord>>

    @Query("SELECT SUM(usageDurationMillis) FROM daily_app_usage WHERE dateKey = :dateKey")
    fun getTotalUsageForDateFlow(dateKey: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: DailyAppUsageRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DailyAppUsageRecord>)
}
