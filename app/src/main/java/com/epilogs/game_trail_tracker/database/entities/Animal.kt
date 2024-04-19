package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImagePathListConverter
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Hunt::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("huntId"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Weapon::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("weaponId"),
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Animal(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var name: String,
    var weight: Double,
    var weightType: String,
    var measurement: Double,
    var measurementType: String,
    @TypeConverters(DateConverter::class) var harvestDate: Date?,
    var notes: String,
    var huntId: Int?,
    var weaponId: Int?,
    @TypeConverters(ImagePathListConverter::class) var imagePaths: List<String>?
)