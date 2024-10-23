package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MeasurementCategory::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("measurementCategoryId"),
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class MeasurementType(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var measurementCategoryId: Int,
)