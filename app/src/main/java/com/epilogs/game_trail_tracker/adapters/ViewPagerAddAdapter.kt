package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.add.AnimalAddFragment
import com.epilogs.game_trail_tracker.fragments.add.LocationAddFragment
import com.epilogs.game_trail_tracker.fragments.add.WeaponAddFragment

class ViewPagerAddAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = arrayOf(LocationAddFragment(), AnimalAddFragment(), WeaponAddFragment())
    private val fragmentTitles = arrayOf("Location", "Animal", "Weapon") // Custom titles

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    // Method to get title based on position
    fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }
}
