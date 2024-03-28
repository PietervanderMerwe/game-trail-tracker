package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.database.entities.Location

interface  OnHuntItemClickListener {
    fun onHuntItemClick(location: Location)
}