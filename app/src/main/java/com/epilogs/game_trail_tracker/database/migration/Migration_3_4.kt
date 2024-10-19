package com.epilogs.game_trail_tracker.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            // Add missing columns to the old "Animal" table
            db.execSQL("ALTER TABLE Animal ADD COLUMN antlerPoints INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE Animal ADD COLUMN antlerLength REAL DEFAULT NULL")
            db.execSQL("ALTER TABLE Animal ADD COLUMN antlerCircumference REAL DEFAULT NULL")
            db.execSQL("ALTER TABLE Animal ADD COLUMN hornLength REAL DEFAULT NULL")
            db.execSQL("ALTER TABLE Animal ADD COLUMN hornBaseCircumference REAL DEFAULT NULL")
            db.execSQL("ALTER TABLE Animal ADD COLUMN skullLength REAL DEFAULT NULL")
            db.execSQL("ALTER TABLE Animal ADD COLUMN skullWidth REAL DEFAULT NULL")
            db.execSQL("ALTER TABLE Animal ADD COLUMN category TEXT NOT NULL DEFAULT ''")

            // Create the new table with the correct foreign key order and schema
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS Animal_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    weight REAL,
                    weightUnit TEXT,
                    antlerPoints INTEGER,
                    antlerLength REAL,
                    antlerCircumference REAL,
                    hornLength REAL,
                    hornBaseCircumference REAL,
                    skullLength REAL,
                    skullWidth REAL,
                    harvestDate INTEGER,
                    notes TEXT,
                    huntId INTEGER,
                    weaponId INTEGER,
                    imagePaths TEXT,
                    category TEXT NOT NULL,
                    FOREIGN KEY (huntId) REFERENCES Hunt(id) ON DELETE SET NULL ON UPDATE NO ACTION,
                    FOREIGN KEY (weaponId) REFERENCES Weapon(id) ON DELETE SET NULL ON UPDATE NO ACTION
                )
            """)

            // Copy the data from the old table to the new table
            db.execSQL("""
                INSERT INTO Animal_new (id, name, weight, weightUnit, antlerPoints, antlerLength, antlerCircumference, hornLength, hornBaseCircumference, skullLength, skullWidth, harvestDate, notes, huntId, weaponId, imagePaths, category)
                SELECT id, name, weight, weightType, antlerPoints, antlerLength, antlerCircumference, hornLength, hornBaseCircumference, skullLength, skullWidth, harvestDate, notes, huntId, weaponId, imagePaths, category
                FROM Animal
            """)

            // Drop the old table
            db.execSQL("DROP TABLE Animal")

            // Rename the new table to "Animal"
            db.execSQL("ALTER TABLE Animal_new RENAME TO Animal")

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}