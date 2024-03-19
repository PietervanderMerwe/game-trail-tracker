package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.utils.ImagePathListConverter

@Entity
data class Weapon (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var name: String,
    var notes: String,
    @TypeConverters(ImagePathListConverter::class) var imagePaths: List<String>?
)
