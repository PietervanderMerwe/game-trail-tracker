package com.epilogs.game_trail_tracker.database

import android.content.Context
import androidx.room.Room
import com.epilogs.game_trail_tracker.database.migration.MIGRATION_3_4

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "game_trail_tracker_db"
            )
                .addMigrations(MIGRATION_3_4)
                .build()
            INSTANCE = instance
            instance
        }
    }
}