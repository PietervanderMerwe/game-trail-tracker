package com.epilogs.game_trail_tracker.fragments.hunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.databinding.FragmentHuntViewDetailBinding
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HuntViewDetailFragment : Fragment() {

    private var huntId: Int? = null
    private val viewModel: HuntViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentLocation: Hunt? = null
    private lateinit var binding: FragmentHuntViewDetailBinding
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
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

    }

    private fun getHunt() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        viewModel.getHuntById(huntId!!).observe(viewLifecycleOwner) { hunt ->
            binding.locationName.text = hunt?.name
            binding.dateRange.text = (hunt?.startDate?.let { dateFormat.format(it) } ?: "N/A") + " - " + (hunt?.endDate?.let { dateFormat.format(it) } ?: "N/A")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(locationId: Int) =
            HuntViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("locationId", locationId)
                }
            }
    }
}