package com.personal.focus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.personal.focus.data.model.StudyChannel
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyChannelDao {

    @Query("SELECT * FROM study_channels ORDER BY channelName ASC")
    fun getAllChannelsFlow(): Flow<List<StudyChannel>>

    @Query("SELECT * FROM study_channels WHERE isWhitelisted = 1")
    suspend fun getAllWhitelistedChannels(): List<StudyChannel>

    @Query("SELECT * FROM study_channels WHERE LOWER(channelName) = LOWER(:name) OR LOWER(channelHandle) = LOWER(:handle) LIMIT 1")
    suspend fun findChannel(name: String, handle: String): StudyChannel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: StudyChannel): Long

    @Query("DELETE FROM study_channels WHERE id = :id")
    suspend fun deleteChannel(id: Long)
}
