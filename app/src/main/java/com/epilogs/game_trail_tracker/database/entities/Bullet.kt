package com.epilogs.game_trail_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.epilogs.game_trail_tracker.utils.ImagePathListConverter

@Entity
data class Bullet (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var type: String, // determines store-bought or self-load
    var manufacturer: String?, // store-bought, nullable for self-load
    var bulletBrand: String, // used in self load e.g. Lapua, Hornady
    var bulletWeight: Double, // in grains, applicable to both
    var bulletType: String, // e.g., hollow point, FMJ, etc.
    var caseBrand: String?, // applicable for self-loaded bullets
    var powderName: String?, // applicable for self-loaded bullets
    var powderWeight: Double?, // in grains, applicable for self-loaded bullets
    var notes: String, // additional information
    var weaponId: Int?, // link to weapon
    @TypeConverters(ImagePathListConverter::class) var imagePaths: List<String>?
)