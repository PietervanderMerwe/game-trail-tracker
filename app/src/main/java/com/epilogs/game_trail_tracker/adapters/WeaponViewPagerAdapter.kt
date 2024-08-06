package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponBulletLoadListFragment
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponImageListFragment

class WeaponViewPagerAdapter(fragmentActivity: FragmentActivity, private val weaponId: Int?) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WeaponBulletLoadListFragment.newInstance(weaponId)
            1 -> WeaponImageListFragment.newInstance(weaponId)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}