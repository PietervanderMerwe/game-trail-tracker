package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weapon (
    @PrimaryKey val id: Int,
    val name: String,
    val notes: String
)
