package com.daybrief.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DailyBriefEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class DayBriefDatabase : RoomDatabase() {
    abstract fun dailyBriefDao(): DailyBriefDao
}

fun createDayBriefDatabase(context: Context): DayBriefDatabase =
    Room.databaseBuilder(
        context.applicationContext,
        DayBriefDatabase::class.java,
        "daybrief.db"
    )
        .fallbackToDestructiveMigration()
        .build()
