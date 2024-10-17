package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.database.entities.Arrow

interface OnArrowItemClickListener {
    fun onArrowItemClick(arrow: Arrow)
}