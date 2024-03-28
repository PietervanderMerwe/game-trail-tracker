package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.trophy.TrophyViewFragment
import com.epilogs.game_trail_tracker.fragments.hunt.HuntViewDetailFragment
import com.epilogs.game_trail_tracker.fragments.hunt.HuntViewFragment
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponViewFragment

class ViewPagerViewAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentTitles = arrayOf("Location", "Animal", "Weapon")

    override fun getItemCount(): Int = 3 // Number of fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HuntViewFragment()
            1 -> TrophyViewFragment()
            2 -> WeaponViewFragment()
            3 -> HuntViewDetailFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }

    fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }
}