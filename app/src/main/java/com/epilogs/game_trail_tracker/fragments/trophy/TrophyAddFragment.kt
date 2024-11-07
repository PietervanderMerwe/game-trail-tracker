package com.epilogs.game_trail_tracker.fragments.trophy

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.HuntDropdownAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponDropdownAdapter
import com.epilogs.game_trail_tracker.database.entities.Trophy
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddBinding
import com.epilogs.game_trail_tracker.fragments.trophy.add.TrophyAddBasicDetailsFragmentDirections
import com.epilogs.game_trail_tracker.fragments.trophy.add.TrophyAddMeasurementDetailsFragmentDirections
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.UserSettingsViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TrophyAddFragment : Fragment() {
    private val viewModel: AnimalViewModel by activityViewModels()
    private val selectedImageUris = mutableSetOf<String>()
    private var trophyId: Int? = null
    private var weaponId: Int? = null
    private lateinit var binding: FragmentTrophyAddBinding
    private var huntId: Int? = null
    private val userSettingsViewModel: UserSettingsViewModel by viewModels()
    private val huntViewModel : HuntViewModel by viewModels()
    private val weaponViewModel : WeaponViewModel by viewModels()
    private var currentTrophy: Trophy? = null
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

        setupHuntDropdown()
        setupWeaponDropdown()
        setupWeightSpinner()

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
            //binding.editTextMeasurement.setText(trophy?.measurement.toString())
            binding.editTextDate.setText(trophy?.harvestDate?.let { dateFormat.format(it) }
                ?: "N/A")

            setupImageAdapter(trophy?.imagePaths?.toMutableSet() ?: mutableSetOf())

            selectedImageUris.addAll(trophy?.imagePaths ?: mutableListOf())
        }
    }

    private fun setupSaveListener() {
        val dateConverter = DateConverter()
        binding.buttonSaveAnimal.setOnClickListener {

            val trophy = Trophy(
                name = binding.editTextSpecieName.text.toString(),
                weight = binding.editTextWeight.text.toString().toDoubleOrNull() ?: 0.0,
                harvestDate = dateConverter.parseDate(binding.editTextDate.text.toString()),
                notes = "Some notes",
                huntId = huntId?.takeIf { it > 0 },
                weaponId = weaponId?.takeIf { it > 0 },
                imagePaths = selectedImageUris.toMutableList(),
            )

            if (trophyId == 0) {
                viewModel.insertAnimal(trophy)
            } else {
                trophy.id = trophyId!!
                viewModel.updateAnimal(trophy)
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
                        val action = TrophyAddBasicDetailsFragmentDirections.actionTrophyAddBasicDetailsFragmentToHuntViewDetailFragment(huntId!!)
                        findNavController().navigate(action)
                    }
                    "trophyFragment" -> {
                        val action = TrophyAddBasicDetailsFragmentDirections.actionTrophyAddBasicDetailsFragmentToTrophyFragment()
                        findNavController().navigate(action)
                    }
                    "TrophyDetailFragment" -> {
                        val action = TrophyAddBasicDetailsFragmentDirections.actionTrophyAddBasicDetailsFragmentToTrophyViewDetailFragment(trophyId!!)
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
        imageAdapterSetup.clearImages()
        binding.buttonDeleteWeapon.visibility = View.GONE
    }

    private fun setupHuntDropdown() {
        val huntDropdown = binding.selectHuntDropdown

        huntViewModel.getAllHunts().observe(viewLifecycleOwner) { hunts ->
            val adapter = HuntDropdownAdapter(requireContext(), hunts)
            huntDropdown.setAdapter(adapter)

            huntDropdown.setOnItemClickListener { parent, view, position, id ->
                val selectedHunt = hunts[position]
                huntDropdown.setText(selectedHunt.name, false)
                huntId = selectedHunt.id
            }
        }
    }

    private fun setupWeaponDropdown() {
        val weaponDropdown = binding.selectWeaponDropdown

        weaponViewModel.getAllWeapons().observe(viewLifecycleOwner) { weapons ->
            val adapter = WeaponDropdownAdapter(requireContext(), weapons)
            weaponDropdown.setAdapter(adapter)

            weaponDropdown.setOnItemClickListener { parent, view, position, id ->
                val selectedWeapon = weapons[position]
                weaponDropdown.setText(selectedWeapon.name, false)
                weaponId = selectedWeapon.id
            }
        }
    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesAnimalRecyclerView,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    private fun setupWeightSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.weights_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerWeightUnits.adapter = adapter
        }

        userSettingsViewModel.getUserSettingsById(1).observe(viewLifecycleOwner) { userSetting ->
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
                val action = TrophyAddBasicDetailsFragmentDirections.actionTrophyAddBasicDetailsFragmentToTrophyFragment()
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