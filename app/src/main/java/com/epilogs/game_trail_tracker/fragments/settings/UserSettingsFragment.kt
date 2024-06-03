package com.epilogs.game_trail_tracker.fragments.settings

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.UserSettings
import com.epilogs.game_trail_tracker.databinding.FragmentUserSettingsBinding
import com.epilogs.game_trail_tracker.viewmodels.UserSettingsViewModel

class UserSettingsFragment : Fragment() {

    private var _binding: FragmentUserSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userSettings: UserSettings
    private val userSettingsViewModel: UserSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.primary_color),
                ContextCompat.getColor(requireContext(), R.color.text_primary)
            )
        )

        binding.switchTheme.thumbTintList = colorStateList

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.measurements_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMeasurementUnits.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.weights_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerWeightUnits.adapter = adapter
        }

        userSettingsViewModel.getUserSettingsById(1).observe(viewLifecycleOwner) { userSetting ->
            userSettings = userSetting!!

            val measurementUnitsAdapter = binding.spinnerMeasurementUnits.adapter as ArrayAdapter<String>
            val measurementUnitsPosition = measurementUnitsAdapter.getPosition(userSettings.measurement)
            binding.spinnerMeasurementUnits.setSelection(measurementUnitsPosition)

            val weightUnitsAdapter = binding.spinnerWeightUnits.adapter as ArrayAdapter<String>
            val weightUnitsPosition = weightUnitsAdapter.getPosition(userSettings.weight)
            binding.spinnerWeightUnits.setSelection(weightUnitsPosition)

            binding.switchTheme.isChecked = userSettings.theme == "dark_mode"
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                userSettings.theme = "dark_mode";
            } else {
                userSettings.theme = "light_mode";
            }

            userSettingsViewModel.updateUserSettings(userSettings)
        }

        binding.spinnerMeasurementUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedMeasurement = parent.getItemAtPosition(position).toString()
                userSettings.measurement = selectedMeasurement
                userSettingsViewModel.updateUserSettings(userSettings)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        binding.spinnerWeightUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedWeight = parent.getItemAtPosition(position).toString()
                userSettings.weight = selectedWeight
                userSettingsViewModel.updateUserSettings(userSettings)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            UserSettingsFragment().apply {
            }
    }
}