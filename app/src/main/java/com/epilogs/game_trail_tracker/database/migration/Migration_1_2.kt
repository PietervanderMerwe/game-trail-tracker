package com.epilogs.game_trail_tracker.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `Hunt` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `name` TEXT NOT NULL,
                `startDate` INTEGER,
                `endDate` INTEGER,
                `imagePaths` TEXT
            )
        """)

            // Create the `Animal` table with foreign key references to `Hunt` and `Weapon`
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `Animal` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `name` TEXT NOT NULL,
                `weight` REAL NOT NULL,
                `weightType` TEXT NOT NULL,
                `measurement` REAL NOT NULL,
                `measurementType` TEXT NOT NULL,
                `harvestDate` INTEGER,
                `notes` TEXT NOT NULL,
                `huntId` INTEGER,
                `weaponId` INTEGER,
                `imagePaths` TEXT,
                FOREIGN KEY(`huntId`) REFERENCES `Hunt`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL,
                FOREIGN KEY(`weaponId`) REFERENCES `Weapon`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
            )
        """)

            // Create the `Weapon` table
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `Weapon` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `name` TEXT NOT NULL,
                `notes` TEXT NOT NULL,
                `imagePaths` TEXT
            )
        """)

            // Create the `UserSettings` table
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `UserSettings` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `theme` TEXT NOT NULL,
                `measurement` TEXT NOT NULL,
                `weight` TEXT NOT NULL
            )
        """)

            // Create room master table
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)
        """)

            // Insert identity hash into the room master table (to track the schema version)
            db.execSQL("""
            INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES(42, '738c0650632ca83dae4c3125b23f8dd0')
        """)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}