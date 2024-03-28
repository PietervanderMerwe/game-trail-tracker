package com.epilogs.game_trail_tracker.data

import java.io.Serializable
import java.util.Date

data class TrophyFilterCriteria(
    var startDate: Date? = null,
    var endDate: Date? = null
) : Serializable