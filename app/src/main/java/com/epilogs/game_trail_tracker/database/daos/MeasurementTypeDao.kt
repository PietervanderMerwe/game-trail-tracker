package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.MeasurementType

@Dao
interface MeasurementTypeDao {
    @Insert
    suspend fun insertMeasurementType(measurementType: MeasurementType) : Long
    @Update
    suspend fun updateMeasurementType(measurementType: MeasurementType)
    @Delete
    suspend fun deleteMeasurementType(measurementType: MeasurementType)
    @Query("SELECT * FROM MeasurementType")
    suspend fun getAllMeasurementTypes(): List<MeasurementType>
    @Query("SELECT * FROM MeasurementType WHERE id = :measurementTypeId")
    suspend fun getMeasurementTypeById(measurementTypeId: Int): MeasurementType?
}