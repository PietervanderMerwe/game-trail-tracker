package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bullet (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var bulletBrand: String,
    var bulletGrain: Double,
    var caseBrand: String,
    var cartridgeLoad: Double,
)