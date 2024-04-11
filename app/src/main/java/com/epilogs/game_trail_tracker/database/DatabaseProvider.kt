package com.epilogs.game_trail_tracker.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "game_trail_tracker_db"
            ).fallbackToDestructiveMigration().build()
            INSTANCE = instance
            instance
        }
    }
}