package com.epilogs.game_trail_tracker.fragments.trophy.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.MeasurementType
import com.epilogs.game_trail_tracker.database.entities.Trophy
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddMeasurementDetailsBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.viewmodels.MeasurementTypeViewModel
import com.epilogs.game_trail_tracker.viewmodels.TrophyAddSharedViewModel
import com.google.android.material.textfield.TextInputLayout

class TrophyAddMeasurementDetailsFragment : Fragment() {

    private val viewModel: TrophyAddSharedViewModel by activityViewModels()
    private val measurementTypeViewModel: MeasurementTypeViewModel by viewModels()
    private lateinit var binding: FragmentTrophyAddMeasurementDetailsBinding
    private lateinit var editTexts : List<TextInputLayout>
    private lateinit var measurementTypeList: List<MeasurementType?>

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
        editTexts = listOf(binding.editTextMeasurementOneContainer, binding.editTextMeasurementTwoContainer, binding.editTextMeasurementThreeContainer, binding.editTextMeasurementFourContainer);
        setUpEditTextFields()
        setupNextPageListener()
    }

    private fun setUpEditTextFields() {
        viewModel.getMeasurementCategoryId().observe(viewLifecycleOwner) { measurementCategoryId ->
            measurementTypeViewModel.getAllMeasurementTypesByCategoryId(measurementCategoryId)
                .observe(viewLifecycleOwner) { measurementTypes ->
                    measurementTypeList = measurementTypes

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

    private fun setupNextPageListener() {
        binding.buttonNextAddAnimal.setOnClickListener {

            //viewModel.setTrophyMeasurements()

            val action = TrophyAddBasicDetailsFragmentDirections.actionTrophyAddBasicDetailsFragmentToTrophyAddMeasurementDetailsFragment()
            findNavController().navigate(action)
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