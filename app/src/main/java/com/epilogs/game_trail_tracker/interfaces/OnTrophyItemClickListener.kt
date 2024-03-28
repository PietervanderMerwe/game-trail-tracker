package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.database.entities.Animal

interface OnTrophyItemClickListener {
    fun onTrophyItemClick(animal: Animal)
}