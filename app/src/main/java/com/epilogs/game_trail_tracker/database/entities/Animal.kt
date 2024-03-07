package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.utils.DateConverter
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("locationId"),
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
    @PrimaryKey val id: Int,
    val name: String,
    val weight: Double,
    val measurement: Double,
    @TypeConverters(DateConverter::class) val harvestDate: Date,
    val notes: String,
    val locationId: Int?,
    val weaponId: Int?
)