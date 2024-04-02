package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.Hunt

@Dao
interface HuntDao {
    @Insert
    suspend fun insertHunt(hunt: Hunt) : Long
    @Update
    suspend fun updateHunt(hunt: Hunt)
    @Delete
    suspend fun deleteHunt(hunt: Hunt)
    @Query("SELECT * FROM Hunt")
    suspend fun getAllHunts(): List<Hunt>
    @Query("SELECT * FROM Hunt WHERE id = :huntId")
    suspend fun getHuntById(huntId: Int): Hunt?

    @Query("SELECT * FROM Hunt ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestHunt(): Hunt?
}