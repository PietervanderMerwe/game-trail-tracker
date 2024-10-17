package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.utils.ImagePathListConverter

@Entity
data class Arrow (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var manufacturer: String?, // store-bought, nullable for self-made
    var shaftMaterial: String, // e.g., carbon, aluminum, wood, etc.
    var length: Double, // in inches, applicable to all arrows
    var fletchingType: String, // e.g., plastic vanes, feathers, etc.
    var pointType: String, // e.g., field point, broadhead, etc.
    var pointWeight: Double?, // in grains, applicable for all arrows
    var nockType: String?, // applicable if the nock is specialized (e.g., illuminated)
    var notes: String, // additional information about the arrow
    var weaponId: Int?, // link to bow or weapon
    @TypeConverters(ImagePathListConverter::class) var imagePaths: List<String>? // for images related to this arrow
)