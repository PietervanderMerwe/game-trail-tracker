package com.epilogs.game_trail_tracker.fragments.trophy.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddMeasurementDetailsBinding
import com.epilogs.game_trail_tracker.viewmodels.MeasurementTypeViewModel
import com.epilogs.game_trail_tracker.viewmodels.TrophyAddSharedViewModel

class TrophyAddMeasurementDetailsFragment : Fragment() {

    private val viewModel: TrophyAddSharedViewModel by activityViewModels()
    private val measurementTypeViewModel: MeasurementTypeViewModel by viewModels()
    private lateinit var binding: FragmentTrophyAddMeasurementDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_add_measurement_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyAddMeasurementDetailsBinding.bind(view)

        setUpEditTextFields()
    }

    private fun setUpEditTextFields() {
        viewModel.getMeasurementCategoryId().observe(viewLifecycleOwner) { measurementCategoryId ->
            measurementTypeViewModel.getAllMeasurementTypesByCategoryId(measurementCategoryId)
                .observe(viewLifecycleOwner) { measurementTypes ->

                    val editTexts = listOf(binding.editTextMeasurementOneContainer, binding.editTextMeasurementTwoContainer, binding.editTextMeasurementThreeContainer, binding.editTextMeasurementFourContainer)

                    for (i in editTexts.indices) {
                        if (i < measurementTypes.size) {
                            editTexts[i].hint = measurementTypes[i]?.name
                            editTexts[i].visibility = View.VISIBLE
                        } else {
                            editTexts[i].visibility = View.GONE
                        }
                    }
                }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TrophyAddMeasurementDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}