package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.MeasurementCategory

@Dao
interface MeasurementCategoryDao {
    @Insert
    suspend fun insertMeasurementCategory(measurementCategory: MeasurementCategory) : Long
    @Update
    suspend fun updateMeasurementCategory(measurementCategory: MeasurementCategory)
    @Delete
    suspend fun deleteMeasurementCategory(measurementCategory: MeasurementCategory)
    @Query("SELECT * FROM MeasurementCategory")
    suspend fun getAllMeasurementCategories(): List<MeasurementCategory>
    @Query("SELECT * FROM MeasurementCategory WHERE id = :measurementCategoryId")
    suspend fun getMeasurementCategoryById(measurementCategoryId: Int): MeasurementCategory?
}