package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeasurementType(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String
)