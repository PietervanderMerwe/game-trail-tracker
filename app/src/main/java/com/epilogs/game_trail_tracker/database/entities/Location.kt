package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.utils.DateConverter
import java.util.Date

@Entity
data class Location (
    @PrimaryKey val id: Int,
    val name: String,
    val isContinues: Boolean,
    @TypeConverters(DateConverter::class) val startDate: Date,
    @TypeConverters(DateConverter::class) val endDate: Date,
    val notes: String
)
