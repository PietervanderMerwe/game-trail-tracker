package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.data.LocationFilterCriteria

interface FilterCriteriaListener {
    fun onFilterCriteriaSelected(criteria: LocationFilterCriteria)
}