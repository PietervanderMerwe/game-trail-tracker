package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MeasurementType::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("measurementTypeId"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Trophy::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("trophyId"),
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TrophyMeasurement (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val trophyId: Int,
    val measurementTypeId: Int,
    var measurement: Double,
)
