package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.Trophy

@Dao
interface TrophyDao {
    @Insert
    suspend fun insertTrophy(trophy: Trophy) : Long
    @Update
    suspend fun updateTrophy(trophy: Trophy)
    @Delete
    suspend fun deleteTrophy(trophy: Trophy)
    @Query("SELECT * FROM Trophy")
    suspend fun getAllTrophies(): List<Trophy>
    @Query("SELECT * FROM Trophy WHERE id = :animalId")
    suspend fun getTrophyById(animalId: Int): Trophy?

    @Query("SELECT * FROM Trophy ORDER BY harvestDate DESC LIMIT 1")
    suspend fun getLatestTrophy(): Trophy?

    @Query("SELECT * FROM Trophy WHERE huntId = :huntId")
    suspend fun getTrophiesByHuntId(huntId: Int): List<Trophy>
}