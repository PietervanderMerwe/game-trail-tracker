package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.trophy.TrophyAddFragment
import com.epilogs.game_trail_tracker.fragments.hunt.HuntAddFragment
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponAddFragment

class ViewPagerAddAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = arrayOf(HuntAddFragment(), TrophyAddFragment(), WeaponAddFragment())
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
