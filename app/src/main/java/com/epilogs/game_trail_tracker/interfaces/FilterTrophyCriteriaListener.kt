package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.data.TrophyFilterCriteria

interface FilterTrophyCriteriaListener {
    fun onFilterCriteriaSelected(criteria: TrophyFilterCriteria?)
}