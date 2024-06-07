package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.hunt.HuntImageListFragment
import com.epilogs.game_trail_tracker.fragments.hunt.HuntTrophyListFragment

class HuntViewPagerAdapter(fragmentActivity: FragmentActivity, private val huntId: Int?) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HuntTrophyListFragment.newInstance(huntId)
            1 -> HuntImageListFragment.newInstance(huntId)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}