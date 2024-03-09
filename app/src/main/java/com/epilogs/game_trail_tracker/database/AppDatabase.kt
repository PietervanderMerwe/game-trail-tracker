package com.epilogs.game_trail_tracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.database.daos.*
import com.epilogs.game_trail_tracker.database.entities.*
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImagePathListConverter

@Database(entities = [Location::class, Animal::class, Weapon::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class, ImagePathListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun animalDao(): AnimalDao
    abstract fun weaponDao(): WeaponDao
}