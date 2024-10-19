package com.epilogs.game_trail_tracker.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `Animal_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `weight` REAL,
                `weightUnit` TEXT,
                `antlerPoints` INTEGER,
                `antlerLength` REAL,
                `antlerCircumference` REAL,
                `hornLength` REAL,
                `hornBaseCircumference` REAL,
                `skullLength` REAL,
                `skullWidth` REAL,
                `harvestDate` INTEGER,
                `notes` TEXT,
                `huntId` INTEGER,
                `weaponId` INTEGER,
                `imagePaths` TEXT,
                FOREIGN KEY(`huntId`) REFERENCES `Hunt`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL,
                FOREIGN KEY(`weaponId`) REFERENCES `Weapon`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
            )
        """)

            // Copy existing data from the old `Animal` table into the new `Animal_new` table
            db.execSQL("""
            INSERT INTO `Animal_new` (id, name, weight, weightUnit, harvestDate, notes, huntId, weaponId, imagePaths)
            SELECT id, name, weight, NULL AS weightUnit, harvestDate, notes, huntId, weaponId, imagePaths
            FROM `Animal`
        """)

            // Drop the old `Animal` table
            db.execSQL("DROP TABLE `Animal`")

            // Rename the new `Animal_new` table to `Animal`
            db.execSQL("ALTER TABLE `Animal_new` RENAME TO `Animal`")

            // Add a new `Arrow` table
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `Arrow` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `manufacturer` TEXT,
                `shaftMaterial` TEXT NOT NULL,
                `length` REAL NOT NULL,
                `fletchingType` TEXT NOT NULL,
                `pointType` TEXT NOT NULL,
                `pointWeight` REAL,
                `nockType` TEXT,
                `notes` TEXT NOT NULL,
                `weaponId` INTEGER,
                `imagePaths` TEXT
            )
        """)

            // Create the new `room_master_table` for Room's internal schema management
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)
        """)

            // Insert the updated identity hash for version 5
            db.execSQL("""
            INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES(42, '21e3e47655d4fa72a1a8500911f3fac1')
        """)

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}