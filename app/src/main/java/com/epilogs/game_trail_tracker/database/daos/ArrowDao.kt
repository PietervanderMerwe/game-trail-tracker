package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.Arrow
import com.epilogs.game_trail_tracker.database.entities.Bullet

@Dao
interface ArrowDao {
    @Insert
    suspend fun insertArrow(arrow: Arrow) : Long
    @Update
    suspend fun updateArrow(arrow: Arrow)
    @Delete
    suspend fun deleteArrow(arrow: Arrow)
    @Query("SELECT * FROM Arrow")
    suspend fun getAllArrows(): List<Arrow>
    @Query("SELECT * FROM Arrow WHERE id = :arrowId")
    suspend fun getArrowsById(arrowId: Int): Arrow?
    @Query("SELECT * FROM Arrow WHERE weaponId = :weaponId")
    suspend fun getArrowsByWeaponId(weaponId: Int): List<Arrow>
}