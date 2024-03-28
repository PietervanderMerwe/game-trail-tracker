package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.data.HuntFilterCriteria

interface FilterHuntCriteriaListener {
    fun onFilterCriteriaSelected(criteria: HuntFilterCriteria?)
}