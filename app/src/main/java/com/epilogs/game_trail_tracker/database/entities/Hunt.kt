package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImagePathListConverter
import java.util.Date

@Entity
data class Hunt (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var name: String,
    @TypeConverters(DateConverter::class) var startDate: Date?,
    @TypeConverters(DateConverter::class) var endDate: Date?,
    @TypeConverters(ImagePathListConverter::class) var imagePaths: List<String>?
)
