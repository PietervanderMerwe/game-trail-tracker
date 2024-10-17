package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.weapon.bullet.WeaponBulletLoadListFragment
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponImageListFragment
import com.epilogs.game_trail_tracker.fragments.weapon.arrow.WeaponArrowLoadListFragment

class WeaponViewPagerAdapter(fragmentActivity: FragmentActivity,
                             private val weaponId: Int?,
                             private val showBullets: Boolean) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Adjust which fragment to show based on the dynamic position
        return when (position) {
            0 -> {
                if (showBullets) {
                    WeaponBulletLoadListFragment.newInstance(weaponId)
                } else {
                    WeaponArrowLoadListFragment.newInstance(weaponId)
                }
            }
            1 -> {
                WeaponImageListFragment.newInstance(weaponId)
            }
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}