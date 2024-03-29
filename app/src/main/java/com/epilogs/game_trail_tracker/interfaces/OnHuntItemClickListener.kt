package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.database.entities.Hunt

interface  OnHuntItemClickListener {
    fun onHuntItemClick(location: Hunt)
}