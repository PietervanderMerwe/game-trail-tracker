package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Bullet

@Dao
interface BulletDao {
    @Insert
    suspend fun insertBullet(bullet: Bullet) : Long
    @Update
    suspend fun updateBullet(bullet: Bullet)
    @Delete
    suspend fun deleteBullet(bullet: Bullet)
    @Query("SELECT * FROM Bullet")
    suspend fun getAllBullets(): List<Bullet>
    @Query("SELECT * FROM Bullet WHERE id = :bulletId")
    suspend fun getBulletById(bulletId: Int): Bullet?
    @Query("SELECT * FROM Bullet WHERE weaponId = :weaponId")
    suspend fun getBulletsByWeaponId(weaponId: Int): List<Bullet>
}