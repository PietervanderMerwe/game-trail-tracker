package com.epilogs.game_trail_tracker.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Hunt` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `name` TEXT NOT NULL,
                `startDate` INTEGER,
                `endDate` INTEGER,
                `imagePaths` TEXT
            )
        """
            )

            // Create `Animal` table with foreign key references
            db.execSQL(
                """
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
        """
            )

            // Create `Weapon` table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Weapon` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `name` TEXT NOT NULL,
                `notes` TEXT NOT NULL,
                `imagePaths` TEXT
            )
        """
            )

            // Create `UserSettings` table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `UserSettings` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `theme` TEXT NOT NULL,
                `measurement` TEXT NOT NULL,
                `weight` TEXT NOT NULL
            )
        """
            )

            // Create `Bullet` table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Bullet` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `type` TEXT NOT NULL,
                `manufacturer` TEXT,
                `bulletBrand` TEXT NOT NULL,
                `bulletWeight` REAL NOT NULL,
                `bulletType` TEXT NOT NULL,
                `caseBrand` TEXT,
                `powderName` TEXT,
                `powderWeight` REAL,
                `notes` TEXT NOT NULL,
                `weaponId` INTEGER,
                `imagePaths` TEXT
            )
        """
            )

            // Insert into room master table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)
        """
            )

            // Insert the identity hash (replace this with the actual hash)
            db.execSQL(
                """
            INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '17c483d3b3ad1940fa56ad8c99149828')
        """
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}