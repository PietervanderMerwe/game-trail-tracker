package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.data.AnimalFilterCriteria

interface FilterAnimalCriteriaListener {
    fun onFilterCriteriaSelected(criteria: AnimalFilterCriteria?)
}