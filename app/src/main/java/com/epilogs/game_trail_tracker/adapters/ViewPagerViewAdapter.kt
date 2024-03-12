package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.Fragments.View.AnimalViewFragment
import com.epilogs.game_trail_tracker.Fragments.View.LocationViewFragment
import com.epilogs.game_trail_tracker.Fragments.View.WeaponViewFragment

class ViewPagerViewAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = arrayOf(LocationViewFragment(), AnimalViewFragment(), WeaponViewFragment())
    private val fragmentTitles = arrayOf("Location", "Animal", "Weapon")

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
    fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }
}