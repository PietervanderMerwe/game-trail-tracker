package com.epilogs.game_trail_tracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epilogs.game_trail_tracker.fragments.trophy.TrophyDetailsTabFragment
import com.epilogs.game_trail_tracker.fragments.trophy.TrophyImageTabFragment

class TrophyViewPagerAdapter(fragmentActivity: FragmentActivity, private val trophyId: Int?) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TrophyDetailsTabFragment.newInstance(trophyId)
            1 -> TrophyImageTabFragment.newInstance(trophyId)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}