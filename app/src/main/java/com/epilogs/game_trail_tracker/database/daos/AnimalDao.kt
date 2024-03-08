package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.Animal

@Dao
interface AnimalDao {
    @Insert
    suspend fun insertAnimal(animal: Animal)
    @Update
    suspend fun updateAnimal(animal: Animal)
    @Delete
    suspend fun deleteAnimal(animal: Animal)
    @Query("SELECT * FROM Animal")
    suspend fun getAllAnimals(): List<Animal>
    @Query("SELECT * FROM Animal WHERE id = :animalId")
    suspend fun getAnimalById(animalId: Int): Animal?
}