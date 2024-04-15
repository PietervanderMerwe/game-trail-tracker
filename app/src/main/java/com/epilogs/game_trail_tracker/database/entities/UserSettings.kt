package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserSettings (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var theme: String,
    var measurementType: String,
)