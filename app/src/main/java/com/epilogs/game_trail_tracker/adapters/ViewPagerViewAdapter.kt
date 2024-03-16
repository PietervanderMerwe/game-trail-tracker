package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.view.AnimalViewFragment
import com.epilogs.game_trail_tracker.fragments.view.LocationViewDetailFragment
import com.epilogs.game_trail_tracker.fragments.view.LocationViewFragment
import com.epilogs.game_trail_tracker.fragments.view.WeaponViewFragment

class ViewPagerViewAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentTitles = arrayOf("Location", "Animal", "Weapon")

    override fun getItemCount(): Int = 3 // Number of fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LocationViewFragment()
            1 -> AnimalViewFragment()
            2 -> WeaponViewFragment()
            3 -> LocationViewDetailFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }

    fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }
}