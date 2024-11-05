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
import com.epilogs.game_trail_tracker.adapters.HuntDropdownAdapter
import com.epilogs.game_trail_tracker.adapters.MeasurementCategoryDropdownAdapter
import com.epilogs.game_trail_tracker.adapters.MeasurementTypeDropdownAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponDropdownAdapter
import com.epilogs.game_trail_tracker.database.entities.Trophy
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddBasicDetailsBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.MeasurementCategoryViewModel
import com.epilogs.game_trail_tracker.viewmodels.MeasurementTypeViewModel
import com.epilogs.game_trail_tracker.viewmodels.TrophyAddSharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import java.util.Calendar

class TrophyAddBasicDetailsFragment : Fragment() {

    private val viewModel: TrophyAddSharedViewModel by activityViewModels()
    private val huntViewModel: HuntViewModel by viewModels()
    private val weaponViewModel: WeaponViewModel by viewModels()
    private val measurementCategoryViewModel: MeasurementCategoryViewModel by viewModels()
    private var trophyId: Int? = null
    private var weaponId: Int? = null
    private var huntId: Int? = null
    private var measurementCategoryId: Int? = null
    private lateinit var binding: FragmentTrophyAddBasicDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trophyId = it.getInt("trophyId")
            huntId = it.getInt("huntId")
            weaponId = it.getInt("weaponId")
            measurementCategoryId = it.getInt("measurementCategoryId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_add_basic_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrophyAddBasicDetailsBinding.bind(view)

        setupHuntDropdown()
        setupWeaponDropdown()
        setupMeasurementDropdown()
        setupDatePicker()
        setupNextPageListener()
        setArguments()
    }

    private fun setupMeasurementDropdown() {
        val measurementCategoryDropdown = binding.trophyMeasurementCategoryDropdown

        measurementCategoryViewModel.getAllMeasurementCategories().observe(viewLifecycleOwner) { measurementCategory ->
            val adapter = MeasurementCategoryDropdownAdapter(requireContext(), measurementCategory)
            measurementCategoryDropdown.setAdapter(adapter)

            val selectedMeasurementCategory = measurementCategory.find { it.id == weaponId }
            selectedMeasurementCategory?.let {
                measurementCategoryDropdown.setText(it.name, false)
            }

            measurementCategoryDropdown.setOnItemClickListener { parent, view, position, id ->
                val selected = measurementCategory[position]
                measurementCategoryDropdown.setText(selected.name, false)
                measurementCategoryId = selected.id
            }
        }
    }

    private fun setupHuntDropdown() {
        val huntDropdown = binding.selectHuntDropdown

        huntViewModel.getAllHunts().observe(viewLifecycleOwner) { hunts ->
            val adapter = HuntDropdownAdapter(requireContext(), hunts)
            huntDropdown.setAdapter(adapter)

            val selectedHunt = hunts.find { it.id == huntId }
            selectedHunt?.let {
                huntDropdown.setText(it.name, false)
            }

            huntDropdown.setOnItemClickListener { parent, view, position, id ->
                val selected = hunts[position]
                huntDropdown.setText(selected.name, false)
                huntId = selected.id
            }
        }
    }

    private fun setupWeaponDropdown() {
        val weaponDropdown = binding.selectWeaponDropdown

        weaponViewModel.getAllWeapons().observe(viewLifecycleOwner) { weapons ->
            val adapter = WeaponDropdownAdapter(requireContext(), weapons)
            weaponDropdown.setAdapter(adapter)

            val selectedWeapon = weapons.find { it.id == weaponId }
            selectedWeapon?.let {
                weaponDropdown.setText(it.name, false)
            }

            weaponDropdown.setOnItemClickListener { parent, view, position, id ->
                val selected = weapons[position]
                weaponDropdown.setText(selected.name, false)
                weaponId = selected.id
            }
        }
    }

    private fun setupDatePicker() {
        if (huntId != 0 && huntId != null) {
            this.huntViewModel.getHuntById(this.huntId!!).observe(viewLifecycleOwner) { hunt ->
                val startCalendar = Calendar.getInstance()
                val endCalendar = Calendar.getInstance()

                hunt?.startDate?.let {
                    startCalendar.time = it
                }
                hunt?.endDate?.let {
                    endCalendar.time = it
                }

                binding.editTextDate.setOnClickListener {
                    showDatePickerDialog(
                        requireContext(), { selectedDate ->
                            binding.editTextDate.setText(selectedDate)
                        }, minDate = if (hunt?.startDate != null) startCalendar else null,
                        maxDate = if (hunt?.endDate != null) endCalendar else null
                    )
                }
            }
        } else {
            binding.editTextDate.setOnClickListener {
                showDatePickerDialog(requireContext(), { selectedDate ->
                    binding.editTextDate.setText(selectedDate)
                })
            }
        }
    }

    private fun setupNextPageListener() {
        val dateConverter = DateConverter()
        binding.buttonNextAddAnimal.setOnClickListener {

            val trophy = Trophy(
                name = binding.editTextSpecieName.text.toString(),
                harvestDate = dateConverter.parseDate(binding.editTextDate.text.toString()),
                huntId = huntId?.takeIf { it > 0 },
                weaponId = weaponId?.takeIf { it > 0 },
            )

            viewModel.setBasicTrophyDetails(trophy, measurementCategoryId)

            val action = TrophyAddBasicDetailsFragmentDirections.actionTrophyAddBasicDetailsFragmentToTrophyAddMeasurementDetailsFragment()
            findNavController().navigate(action)
        }
    }

    private fun clearScreen() {
        binding.editTextSpecieName.text.clear()
        binding.editTextDate.text.clear()
    }

    private fun setArguments() {
        viewModel.setArguments(trophyId, huntId, arguments?.getString("originFragment"))
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            TrophyAddBasicDetailsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}