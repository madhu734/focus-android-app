package com.personal.focus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.personal.focus.data.dao.AppBlockDao
import com.personal.focus.data.dao.DailyUsageDao
import com.personal.focus.data.dao.FocusTimerDao
import com.personal.focus.data.dao.StudyChannelDao
import com.personal.focus.data.model.AppBlockRule
import com.personal.focus.data.model.DailyAppUsageRecord
import com.personal.focus.data.model.FocusTimerSession
import com.personal.focus.data.model.StudyChannel

@Database(
    entities = [
        FocusTimerSession::class,
        AppBlockRule::class,
        StudyChannel::class,
        DailyAppUsageRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {

    abstract fun focusTimerDao(): FocusTimerDao
    abstract fun appBlockDao(): AppBlockDao
    abstract fun studyChannelDao(): StudyChannelDao
    abstract fun dailyUsageDao(): DailyUsageDao

    companion object {
        @Volatile
        private var INSTANCE: FocusDatabase? = null

        fun getInstance(context: Context): FocusDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext ?: context,
                    FocusDatabase::class.java,
                    "focus_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
