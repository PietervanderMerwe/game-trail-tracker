package com.epilogs.game_trail_tracker.interfaces

import com.epilogs.game_trail_tracker.database.entities.Bullet

interface OnBulletItemClickListener {
    fun onBulletItemClick(bullet: Bullet)
}