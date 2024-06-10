package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.Weapon

@Dao
interface WeaponDao {
    @Insert
    suspend fun insertWeapon(weapon: Weapon) : Long
    @Update
    suspend fun updateWeapon(weapon: Weapon)
    @Delete
    suspend fun deleteWeapon(weapon: Weapon)
    @Query("SELECT * FROM Weapon")
    suspend fun getAllWeapons(): List<Weapon>
    @Query("SELECT * FROM Weapon WHERE id = :weaponId")
    suspend fun getWeaponById(weaponId: Int): Weapon?
}