package com.epilogs.game_trail_tracker.DTOs.MeasurementTypes

data class TrophyMeasurementWithType(
    var id: Int = 0,
    val trophyId: Int,
    val measurementTypeId: Int,
    var measurement: Double,
    val name: String
)