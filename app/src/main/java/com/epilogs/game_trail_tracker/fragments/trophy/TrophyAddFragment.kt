package com.epilogs.game_trail_tracker.fragments.trophy

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.LocationAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.UserSettingsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TrophyAddFragment : Fragment() {
    private val viewModel: AnimalViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val selectedImageUris = mutableSetOf<String>()
    private var trophyId: Int? = null
    private var weaponId: Int? = null
    private lateinit var binding: FragmentTrophyAddBinding
    private var huntId: Int? = null
    private val userSettingsViewModel: UserSettingsViewModel by viewModels()
    private val huntViewModel : HuntViewModel by viewModels()
    private var currentTrophy: Animal? = null
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trophyId = it.getInt("trophyId")
            huntId = it.getInt("huntId")
            weaponId = it.getInt("weaponId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrophyAddBinding.bind(view)

        setupAddUI()
    }

    private fun setupAddUI() {

        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
            }
        )

        binding.buttonSelectAnimalImages.setOnClickListener {
            imagePickerSetup.pickImages(selectedImageUris.toMutableList())
        }

        setupHuntSpinner(binding.spinnerHunt)
        setupWeaponSpinner(binding.spinnerWeapon)
        setupWeightAndMeasurementSpinner()

        if (trophyId == 0) setupImageAdapter(selectedImageUris)

        setupSaveListener()
        setupObserveInsertion()
        setupObserveUpdate()
        setupDatePicker()
        if (trophyId != 0) {
            setupEditScreen()
        }
    }

    private fun setupEditScreen() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.addTrophyText.text = getString(R.string.update_trophy)
        binding.buttonSaveAnimal.text = getString(R.string.button_update)
        binding.buttonDeleteWeapon.visibility = View.VISIBLE

        binding.buttonDeleteWeapon.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        viewModel.getAnimalById(trophyId!!).observe(viewLifecycleOwner) { trophy ->
            currentTrophy = trophy
            binding.editTextSpecieName.setText(trophy?.name)
            binding.editTextWeight.setText(trophy?.weight.toString())
            binding.editTextMeasurement.setText(trophy?.measurement.toString())
            binding.editTextDate.setText(trophy?.harvestDate?.let { dateFormat.format(it) }
                ?: "N/A")

            setupImageAdapter(trophy?.imagePaths?.toMutableSet() ?: mutableSetOf())

            selectedImageUris.addAll(trophy?.imagePaths ?: mutableListOf())
        }
    }

    private fun setupSaveListener() {
        val dateConverter = DateConverter()
        binding.buttonSaveAnimal.setOnClickListener {

            val animal = Animal(
                name = binding.editTextSpecieName.text.toString(),
                weight = binding.editTextWeight.text.toString().toDoubleOrNull() ?: 0.0,
                weightType = binding.spinnerWeightUnits.selectedItem.toString(),
                measurement = binding.editTextMeasurement.text.toString().toDoubleOrNull() ?: 0.0,
                measurementType = binding.spinnerMeasurementUnits.selectedItem.toString(),
                harvestDate = dateConverter.parseDate(binding.editTextDate.text.toString()),
                notes = "Some notes",
                huntId = huntId?.takeIf { it > 0 },
                weaponId = weaponId?.takeIf { it > 0 },
                imagePaths = selectedImageUris.toMutableList()
            )

            if (trophyId == 0) {
                viewModel.insertAnimal(animal)
            } else {
                animal.id = trophyId
                viewModel.updateAnimal(animal)
            }
        }
    }

    private fun setupObserveInsertion() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) {
                viewModel.resetInsertionSuccess()
                clearScreen()
                val originFragment = arguments?.getString("originFragment")
                when (originFragment) {
                    "huntFragment" -> {
                        val action = TrophyAddFragmentDirections.actionTrophyAddFragmentToHuntViewDetailFragment(huntId!!)
                        findNavController().navigate(action)
                    }
                    "trophyFragment" -> {
                        val action = TrophyAddFragmentDirections.actionTrophyAddFragmentToTrophyFragment()
                        findNavController().navigate(action)
                    }
                    "TrophyDetailFragment" -> {
                        val action = TrophyAddFragmentDirections.actionTrophyAddFragmentToTrophyViewDetailFragment(trophyId!!)
                        findNavController().navigate(action)
                    }
                    else -> findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupObserveUpdate() {
        viewModel.getUpdateSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) {
                viewModel.resetUpdateSuccess()
                clearScreen()
                findNavController().navigateUp()
            }
        }
    }

    private fun clearScreen() {
        binding.editTextSpecieName.text.clear()
        binding.editTextDate.text.clear()
        binding.editTextWeight.text.clear()
        binding.editTextMeasurement.text.clear()
        imageAdapterSetup.clearImages()
        binding.buttonDeleteWeapon.visibility = View.GONE
    }

    private fun setupHuntSpinner(spinnerLocation: Spinner) {
        val locations = mutableListOf<Hunt>()
        val locationAdapter = LocationAdapter(requireContext(), locations)
        spinnerLocation.adapter = locationAdapter

        viewModel.getAllLocations().observe(viewLifecycleOwner) { newLocations ->
            val modifiedLocations = mutableListOf<Hunt>().apply {
                add(
                    Hunt(null, "None", null, null, mutableListOf<String>())
                )
                addAll(newLocations)
            }
            locationAdapter.clear()
            locationAdapter.addAll(modifiedLocations)
            locationAdapter.notifyDataSetChanged()

            val defaultPosition = modifiedLocations.indexOfFirst { it.id == huntId }

            if (defaultPosition >= 0) {
                spinnerLocation.setSelection(defaultPosition)
            }
        }

        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedLocation = parent.getItemAtPosition(position) as Hunt
                huntId = selectedLocation.id
                setupDatePicker()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected if needed
            }
        }

        sharedViewModel.getLocationsUpdateSignal().observe(viewLifecycleOwner) { updated ->
            if (updated) {
                viewModel.getAllLocations().observe(viewLifecycleOwner) { newLocations ->
                    newLocations?.let { locations ->
                        locationAdapter.clear()
                        locationAdapter.addAll(locations)
                        locationAdapter.notifyDataSetChanged()
                        sharedViewModel.resetLocationsUpdatedSignal()
                    } ?: run {
                        sharedViewModel.resetLocationsUpdatedSignal()
                    }
                }
            }
        }

        if (huntId != 0) {
            showSpinnerHunt()
        }

        binding.buttonLinkToHunt.setOnClickListener {
            showSpinnerHunt()
        }
    }

    private fun showSpinnerHunt() {
        binding.spinnerHunt.visibility =
            if (binding.spinnerHunt.visibility == View.GONE) View.VISIBLE else View.GONE
        binding.selectHuntTextView.visibility =
            if (binding.selectHuntTextView.visibility == View.GONE) View.VISIBLE else View.GONE
        binding.buttonLinkToHunt.visibility =
            if (binding.buttonLinkToHunt.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun setupWeaponSpinner(spinnerWeapon: Spinner) {

        val weapons = mutableListOf<Weapon>()
        val weaponAdapter = WeaponAdapter(requireContext(), weapons)
        spinnerWeapon.adapter = weaponAdapter

        viewModel.getAllWeapons().observe(viewLifecycleOwner) { newWeapons ->
            val modifiedWeapons = mutableListOf<Weapon>().apply {
                add(Weapon(null, "None", "", mutableListOf<String>()))
                addAll(newWeapons)
            }
            weaponAdapter.clear()
            weaponAdapter.addAll(modifiedWeapons)
            weaponAdapter.notifyDataSetChanged()

            val defaultPosition = modifiedWeapons.indexOfFirst { it.id == weaponId }

            if (defaultPosition >= 0) {
                spinnerWeapon.setSelection(defaultPosition)
            }
        }

        sharedViewModel.getWeaponsUpdateSignal().observe(viewLifecycleOwner) { updated ->
            if (updated) {
                viewModel.getAllWeapons().observe(viewLifecycleOwner) { newWeapons ->
                    newWeapons?.let { weapons ->
                        weaponAdapter.clear()
                        weaponAdapter.addAll(weapons)
                        weaponAdapter.notifyDataSetChanged()
                        sharedViewModel.resetWeaponsUpdatedSignal()
                    } ?: run {
                        sharedViewModel.resetWeaponsUpdatedSignal()
                    }
                }
            }
        }

        spinnerWeapon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedWeapon = parent.getItemAtPosition(position) as Weapon
                weaponId = selectedWeapon.id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected if needed
            }
        }

        if (weaponId != 0) {
            showSpinnerWeapon()
        }

        binding.buttonLinkWeapon.setOnClickListener {
            showSpinnerWeapon()
        }
    }

    private fun showSpinnerWeapon() {
        binding.spinnerWeapon.visibility =
            if (binding.spinnerWeapon.visibility == View.GONE) View.VISIBLE else View.GONE
        binding.selectWeaponTextView.visibility =
            if (binding.selectWeaponTextView.visibility == View.GONE) View.VISIBLE else View.GONE
        binding.buttonLinkWeapon.visibility =
            if (binding.buttonLinkWeapon.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesAnimalRecyclerView,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    private fun setupWeightAndMeasurementSpinner() {
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
            val measurementUnitsAdapter = binding.spinnerMeasurementUnits.adapter as ArrayAdapter<String>
            val measurementUnitsPosition = measurementUnitsAdapter.getPosition(userSetting?.measurement)
            binding.spinnerMeasurementUnits.setSelection(measurementUnitsPosition)

            val weightUnitsAdapter = binding.spinnerWeightUnits.adapter as ArrayAdapter<String>
            val weightUnitsPosition = weightUnitsAdapter.getPosition(userSetting?.weight)
            binding.spinnerWeightUnits.setSelection(weightUnitsPosition)
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
                    showDatePickerDialog(requireContext(), { selectedDate ->
                        binding.editTextDate.setText(selectedDate)
                    }, minDate = if (hunt?.startDate != null) startCalendar else null,
                        maxDate = if (hunt?.endDate != null) endCalendar else null)
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

    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this trophy?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteAnimal(currentTrophy!!)
                val action = TrophyAddFragmentDirections.actionTrophyAddFragmentToTrophyFragment()
                findNavController().navigate(action)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_color))
        }
        dialog.show()
    }
}