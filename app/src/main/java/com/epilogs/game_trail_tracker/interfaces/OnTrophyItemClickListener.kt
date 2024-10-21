package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.database.entities.Trophy

interface OnTrophyItemClickListener {
    fun onTrophyItemClick(trophy: Trophy)
}