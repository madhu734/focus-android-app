package com.personal.focus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personal.focus.data.model.FocusTimerSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusTimerDao {

    @Query("SELECT * FROM focus_timer_session WHERE sessionStatus = 'ACTIVE' OR sessionStatus = 'PAUSED' ORDER BY sessionId DESC LIMIT 1")
    fun getActiveSessionFlow(): Flow<FocusTimerSession?>

    @Query("SELECT * FROM focus_timer_session WHERE sessionStatus = 'ACTIVE' OR sessionStatus = 'PAUSED' ORDER BY sessionId DESC LIMIT 1")
    suspend fun getActiveSession(): FocusTimerSession?

    @Query("SELECT * FROM focus_timer_session ORDER BY sessionId DESC LIMIT 1")
    fun getLatestSessionFlow(): Flow<FocusTimerSession?>

    @Query("SELECT * FROM focus_timer_session ORDER BY startTime DESC")
    fun getAllSessionsFlow(): Flow<List<FocusTimerSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusTimerSession): Long

    @Update
    suspend fun updateSession(session: FocusTimerSession)

    @Query("UPDATE focus_timer_session SET sessionStatus = 'CANCELLED' WHERE sessionStatus = 'ACTIVE' OR sessionStatus = 'PAUSED'")
    suspend fun cancelAllActiveSessions()

    @Query("SELECT SUM(totalFocusTimeSpentMillis) FROM focus_timer_session WHERE sessionStatus = 'COMPLETED'")
    fun getTotalCompletedFocusTimeMillisFlow(): Flow<Long?>
}
