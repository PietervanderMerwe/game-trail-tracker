package com.epilogs.game_trail_tracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.database.daos.*
import com.epilogs.game_trail_tracker.database.entities.*
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImagePathListConverter

@Database(entities = [Hunt::class, Animal::class, Weapon::class, UserSettings::class, Bullet::class], version = 4, exportSchema = true)
@TypeConverters(DateConverter::class, ImagePathListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun huntDao(): HuntDao
    abstract fun animalDao(): AnimalDao
    abstract fun weaponDao(): WeaponDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun bulletDao(): BulletDao
}