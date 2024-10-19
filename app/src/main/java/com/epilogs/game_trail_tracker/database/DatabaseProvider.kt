package com.epilogs.game_trail_tracker.database

import android.content.Context
import androidx.room.Room
import com.epilogs.game_trail_tracker.database.migration.MIGRATION_1_2
import com.epilogs.game_trail_tracker.database.migration.MIGRATION_2_3
import com.epilogs.game_trail_tracker.database.migration.MIGRATION_3_4
import com.epilogs.game_trail_tracker.database.migration.MIGRATION_4_5

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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
            INSTANCE = instance
            instance
        }
    }
}