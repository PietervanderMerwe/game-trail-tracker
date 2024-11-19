package com.epilogs.game_trail_tracker.fragments.trophy.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.MeasurementType
import com.epilogs.game_trail_tracker.database.entities.TrophyMeasurement
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddMeasurementDetailsBinding
import com.epilogs.game_trail_tracker.viewmodels.MeasurementTypeViewModel
import com.epilogs.game_trail_tracker.viewmodels.TrophyAddSharedViewModel
import com.google.android.material.textfield.TextInputLayout

class TrophyAddMeasurementDetailsFragment : Fragment() {

    private val viewModel: TrophyAddSharedViewModel by activityViewModels()
    private val measurementTypeViewModel: MeasurementTypeViewModel by viewModels()
    private lateinit var binding: FragmentTrophyAddMeasurementDetailsBinding
    private lateinit var editTextContainers: List<TextInputLayout>
    private lateinit var editTexts: List<EditText>
    private lateinit var measurementTypeList: List<MeasurementType?>
    private var measurementAmount: Int = 0

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
        editTextContainers = listOf(
            binding.editTextMeasurementOneContainer,
            binding.editTextMeasurementTwoContainer,
            binding.editTextMeasurementThreeContainer,
            binding.editTextMeasurementFourContainer,
            binding.editTextMeasurementFiveContainer,
            binding.editTextMeasurementSixContainer
        )
        editTexts = listOf(
            binding.editTextMeasurementOne,
            binding.editTextMeasurementTwo,
            binding.editTextMeasurementThree,
            binding.editTextMeasurementFour,
            binding.editTextMeasurementFive,
            binding.editTextMeasurementSix
        )
        setUpEditTextFields()
        setupNextPageListener()
    }

    private fun setUpEditTextFields() {
        viewModel.getMeasurementCategoryId().observe(viewLifecycleOwner) { measurementCategoryId ->
            measurementTypeViewModel.getAllMeasurementTypesByCategoryId(measurementCategoryId)
                .observe(viewLifecycleOwner) { measurementTypes ->
                    measurementTypeList = measurementTypes
                    measurementAmount = measurementTypes.size

                    for (i in editTextContainers.indices) {
                        if (i < measurementTypes.size) {
                            editTextContainers[i].hint = measurementTypes[i]?.name
                            editTextContainers[i].visibility = View.VISIBLE
                        } else {
                            editTextContainers[i].visibility = View.GONE
                        }
                    }
                }
        }
    }

    private fun setupNextPageListener() {
        binding.buttonNextAddAnimal.setOnClickListener {

            viewModel.setTrophyMeasurements(getFilledTrophyMeasurements())
            viewModel.setTrophyWeight(binding.editTextWeight.text.toString().toDoubleOrNull() ?: 0.0)

            val action =
                TrophyAddMeasurementDetailsFragmentDirections.actionTrophyAddMeasurementDetailsFragmentToTrophyAddImagesFragment()
            findNavController().navigate(action)
        }
    }

    private fun getFilledTrophyMeasurements(): List<TrophyMeasurement> {
        val trophyMeasurements = mutableListOf<TrophyMeasurement>()

        for (i in editTexts.indices) {
            val editText = editTexts[i]
            val container = editTextContainers[i]

            if (container.visibility == View.VISIBLE) {
                val measurementValue = editText.text.toString().trim()

                if (measurementValue.isNotEmpty()) {
                    trophyMeasurements.add(
                        TrophyMeasurement(
                            measurementTypeId = measurementTypeList[i]?.id!!,
                            trophyId = 0,
                            measurement = measurementValue.toDouble()
                        )
                    )
                }
            }
        }
        return trophyMeasurements
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