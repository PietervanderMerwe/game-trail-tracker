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
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var category: String,
    var weight: Double? = null,
    var weightUnit: String? = null,
    var antlerPoints: Int? = null,
    var antlerLength: Double? = null,
    var antlerCircumference: Double? = null,
    var hornLength: Double? = null,
    var hornBaseCircumference: Double? = null,
    var skullLength: Double? = null,
    var skullWidth: Double? = null,
    @TypeConverters(DateConverter::class) var harvestDate: Date? = null,
    var notes: String? = null,
    var huntId: Int? = null,
    var weaponId: Int? = null,
    @TypeConverters(ImagePathListConverter::class) var imagePaths: List<String>? = null
)