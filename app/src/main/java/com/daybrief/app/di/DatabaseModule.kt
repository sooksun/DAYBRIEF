package com.daybrief.app.di

import android.content.Context
import com.daybrief.app.data.db.DailyBriefDao
import com.daybrief.app.data.db.DayBriefDatabase
import com.daybrief.app.data.db.createDayBriefDatabase
import com.daybrief.app.data.repository.BriefRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DayBriefDatabase =
        createDayBriefDatabase(context)

    @Provides
    @Singleton
    fun provideDailyBriefDao(database: DayBriefDatabase): DailyBriefDao =
        database.dailyBriefDao()

    @Provides
    @Singleton
    fun provideBriefRepository(dao: DailyBriefDao): BriefRepository =
        BriefRepository(dao)
}
