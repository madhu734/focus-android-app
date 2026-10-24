package com.personal.focus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personal.focus.data.model.AppBlockRule
import kotlinx.coroutines.flow.Flow

@Dao
interface AppBlockDao {

    @Query("SELECT * FROM app_block_rules ORDER BY appName ASC")
    fun getAllRulesFlow(): Flow<List<AppBlockRule>>

    @Query("SELECT * FROM app_block_rules WHERE isBlocked = 1")
    fun getBlockedRulesFlow(): Flow<List<AppBlockRule>>

    @Query("SELECT * FROM app_block_rules WHERE packageName = :packageName LIMIT 1")
    suspend fun getRule(packageName: String): AppBlockRule?

    @Query("SELECT * FROM app_block_rules")
    suspend fun getAllRules(): List<AppBlockRule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(rule: AppBlockRule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<AppBlockRule>)

    @Update
    suspend fun update(rule: AppBlockRule)

    @Query("DELETE FROM app_block_rules WHERE packageName = :packageName")
    suspend fun deleteRule(packageName: String)
}
