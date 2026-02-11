package com.daybrief.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyBriefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DailyBriefEntity)

    @Query("SELECT * FROM daily_briefs WHERE dateEpochDay = :epochDay LIMIT 1")
    suspend fun getByDate(epochDay: Long): DailyBriefEntity?

    @Query("SELECT * FROM daily_briefs WHERE dateEpochDay = :epochDay LIMIT 1")
    fun observeByDate(epochDay: Long): Flow<DailyBriefEntity?>

    @Query("SELECT * FROM daily_briefs ORDER BY dateEpochDay DESC")
    fun getAllByDateDesc(): Flow<List<DailyBriefEntity>>

    @Query("SELECT dateEpochDay FROM daily_briefs ORDER BY dateEpochDay DESC")
    fun getAllDatesDesc(): Flow<List<Long>>

    @Delete
    suspend fun delete(entity: DailyBriefEntity)

    @Query("DELETE FROM daily_briefs WHERE dateEpochDay = :epochDay")
    suspend fun deleteByDate(epochDay: Long)

    @Query("DELETE FROM daily_briefs")
    suspend fun deleteAll()
}
