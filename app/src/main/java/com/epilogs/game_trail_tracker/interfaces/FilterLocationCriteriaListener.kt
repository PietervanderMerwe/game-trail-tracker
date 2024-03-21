package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.data.LocationFilterCriteria

interface FilterLocationCriteriaListener {
    fun onFilterCriteriaSelected(criteria: LocationFilterCriteria?)
}