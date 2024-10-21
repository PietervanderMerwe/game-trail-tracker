package com.epilogs.game_trail_tracker.fragments.hunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.HuntViewPagerAdapter
import com.epilogs.game_trail_tracker.databinding.FragmentHuntViewDetailBinding
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Locale

class HuntViewDetailFragment : Fragment() {

    private var huntId: Int? = null
    private val huntViewModel: HuntViewModel by viewModels()
    private lateinit var binding: FragmentHuntViewDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            huntId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHuntViewDetailBinding.bind(view)

        getHunt()
        setButton()
        setAdapter()
    }

    private fun getHunt() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        huntViewModel.getHuntById(huntId!!).observe(viewLifecycleOwner) { hunt ->
            binding.locationName.text = hunt?.name
            val startDateStr = hunt?.startDate?.let { dateFormat.format(it) } ?: "N/A"
            val endDateStr = hunt?.endDate?.let { dateFormat.format(it) } ?: "N/A"
            val dateRange = getString(R.string.date_range, startDateStr, endDateStr)
            binding.dateRange.text = dateRange
        }
    }

    private fun setButton() {
        binding.editIcon.setOnClickListener {
            val action =
                HuntViewDetailFragmentDirections.actionHuntViewDetailFragmentToHuntAddFragment(
                    huntId!!
                )
            findNavController().navigate(action)
        }
    }

    private fun setAdapter() {
        huntId?.let {
            val adapter = HuntViewPagerAdapter(requireActivity(), huntId)
            binding.viewPager.adapter = adapter

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Trophies"
                    1 -> "Images"
                    else -> null
                }
            }.attach()
        }
    }

    companion object {
        fun newInstance(huntId: Int): HuntViewDetailFragment {
            val fragment = HuntViewDetailFragment()
            val args = Bundle()
            args.putInt("huntId", huntId)
            fragment.arguments = args
            return fragment
        }
    }
}