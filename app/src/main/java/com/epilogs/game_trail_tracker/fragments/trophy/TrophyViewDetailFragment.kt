package com.epilogs.game_trail_tracker.fragments.trophy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.TrophyViewPagerAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponViewPagerAdapter
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyViewDetailBinding
import com.google.android.material.tabs.TabLayoutMediator

class TrophyViewDetailFragment : Fragment() {

    private var trophyId: Int? = null
    private lateinit var binding: FragmentTrophyViewDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trophyId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyViewDetailBinding.bind(view)

        setAdapter()
    }

    private fun setAdapter() {
        trophyId?.let {
            val adapter = TrophyViewPagerAdapter(requireActivity(), trophyId)
            binding.viewPager.adapter = adapter

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Details"
                    1 -> "Images"
                    else -> null
                }
            }.attach()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(animalId: Int) =
            TrophyViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("animalId", animalId)
                }
            }
    }
}