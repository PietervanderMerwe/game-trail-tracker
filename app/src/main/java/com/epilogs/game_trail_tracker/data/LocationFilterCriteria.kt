package com.epilogs.game_trail_tracker.data

import java.util.Date

data class LocationFilterCriteria (
    var startDate: Date? = null,
    var endDate: Date? = null
    // Add other filter parameters as needed
)