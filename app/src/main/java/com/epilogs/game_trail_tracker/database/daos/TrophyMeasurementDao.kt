package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.TrophyMeasurement

@Dao
interface TrophyMeasurementDao {
    @Insert
    suspend fun insertTrophyMeasurement(trophyMeasurement: TrophyMeasurement) : Long
    @Insert
    suspend fun insertTrophyMeasurements(trophyMeasurements: List<TrophyMeasurement>): List<Long>
    @Update
    suspend fun updateTrophyMeasurement(trophyMeasurement: TrophyMeasurement)
    @Delete
    suspend fun deleteTrophyMeasurement(trophyMeasurement: TrophyMeasurement)
    @Query("SELECT * FROM TrophyMeasurement")
    suspend fun getAllTrophyMeasurements(): List<TrophyMeasurement>
    @Query("SELECT * FROM TrophyMeasurement WHERE id = :trophyMeasurementId")
    suspend fun getTrophyMeasurementById(trophyMeasurementId: Int): TrophyMeasurement?
}