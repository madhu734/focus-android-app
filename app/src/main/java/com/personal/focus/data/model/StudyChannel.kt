package com.personal.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Whitelisted educational/study YouTube channel entity.
 */
@Entity(tableName = "study_channels")
data class StudyChannel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val channelName: String,
    val channelHandle: String,               // e.g. "@freecodecamp", "@hubermanlab", "@veritasium"
    val isWhitelisted: Boolean = true,
    val addedTimestamp: Long = System.currentTimeMillis()
)
