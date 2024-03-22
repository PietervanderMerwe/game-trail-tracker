package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.Location

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location: Location)
    @Update
    suspend fun updateLocation(location: Location)
    @Delete
    suspend fun deleteLocation(location: Location)
    @Query("SELECT * FROM Location")
    suspend fun getAllLocations(): List<Location>
    @Query("SELECT * FROM Location WHERE id = :locationId")
    suspend fun getLocationById(locationId: Int): Location?

    @Query("SELECT * FROM Location ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestLocation(): Location?
}