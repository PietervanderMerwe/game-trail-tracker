package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.database.entities.Weapon

interface OnWeaponItemClickListener {
    fun onWeaponItemClick(weapon: Weapon)
}