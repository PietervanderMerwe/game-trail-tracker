package com.epilogs.game_trail_tracker.fragments.trophy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.adapters.LocationAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class TrophyAddFragment : Fragment() {
    private val viewModel: AnimalViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val selectedImageUris = mutableListOf<String>()
    private lateinit var imageAdapter: ImagesAdapter
    private lateinit var documentPickerLauncher: ActivityResultLauncher<Array<String>>
    private var trophyId: Int? = null
    private var weaponId: Int? = null
    private lateinit var binding: FragmentTrophyAddBinding
    private var huntId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trophyId = it.getInt("trophyId")
            huntId = it.getInt("huntId")
            weaponId = it.getInt("weaponId")
        }
        documentPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            uris.forEach { uri ->
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireActivity().contentResolver.takePersistableUriPermission(uri, takeFlags)
                selectedImageUris.add(uri.toString())
            }
            imageAdapter.updateImages(selectedImageUris)
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
        binding.editTextDate.setOnClickListener {
            showDatePickerDialog(requireContext(), { selectedDate ->
                binding.editTextDate.setText(selectedDate)
            })
        }

        binding.buttonSelectAnimalImages.setOnClickListener {
            documentPickerLauncher.launch(arrayOf("image/*"))
        }

        setupHuntSpinner(binding.spinnerHunt)
        setupWeaponSpinner(binding.spinnerWeapon)
        setupImageView(binding.imagesAnimalRecyclerView)

        setupSaveListener()
        setupObserveInsertion()
        setupObserveUpdate()
        if (trophyId != 0) {
            setupEditScren()
        }

    }

    private fun setupEditScren() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding.addTrophyText.text = "Update trophy"
        binding.buttonSaveAnimal.text = "Update"

        viewModel.getAnimalById(trophyId!!).observe(viewLifecycleOwner, Observer { trophy ->
            binding.editTextSpecieName.setText(trophy?.name)
            binding.editTextWeight.setText(trophy?.weight.toString())
            binding.editTextMeasurement.setText(trophy?.measurement.toString())
            binding.editTextDate.setText(trophy?.harvestDate?.let { dateFormat.format(it) }
                ?: "N/A")
        })
    }

    private fun setupSaveListener() {
        val dateConverter = DateConverter()
        binding.buttonSaveAnimal.setOnClickListener {

            if (trophyId == 0) {
                Log.d("HuntId", huntId.toString())
                Log.d("HuntId", weaponId.toString())
                val animal = Animal(
                    name = binding.editTextSpecieName.text.toString(),
                    weight = binding.editTextWeight.text.toString().toDoubleOrNull() ?: 0.0,
                    measurement = binding.editTextMeasurement.text.toString().toDoubleOrNull()
                        ?: 0.0,
                    harvestDate = dateConverter.parseDate(binding.editTextDate.text.toString()),
                    notes = "Some notes",
                    huntId = huntId.takeIf { it!! > 0 },
                    weaponId = weaponId.takeIf { it!! > 0 },
                    imagePaths = selectedImageUris
                )

                viewModel.insertAnimal(animal)
            } else {
                val animal = Animal(
                    id = trophyId,
                    name = binding.editTextSpecieName.text.toString(),
                    weight = binding.editTextWeight.text.toString().toDoubleOrNull() ?: 0.0,
                    measurement = binding.editTextMeasurement.text.toString().toDoubleOrNull()
                        ?: 0.0,
                    harvestDate = dateConverter.parseDate(binding.editTextDate.text.toString()),
                    notes = "Some notes",
                    huntId = huntId.takeIf { it!! > 0 },
                    weaponId = weaponId.takeIf { it!! > 0 },
                    imagePaths = selectedImageUris
                )

                viewModel.updateAnimal(animal)
            }
        }
    }

    private fun setupObserveInsertion() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                binding.editTextSpecieName.text.clear()
                binding.editTextDate.text.clear()
                binding.editTextWeight.text.clear()
                binding.editTextMeasurement.text.clear()
                viewModel.resetInsertionSuccess()
                imageAdapter.clearImages()
                findNavController().navigateUp()
            }
        })
    }

    private fun setupObserveUpdate() {
        viewModel.getUpdateSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                binding.editTextSpecieName.text.clear()
                binding.editTextDate.text.clear()
                binding.editTextWeight.text.clear()
                binding.editTextMeasurement.text.clear()
                viewModel.resetInsertionSuccess()
                imageAdapter.clearImages()
                findNavController().navigateUp()
            }
        })
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

    private fun setupImageView(imagesRecyclerView: RecyclerView) {
        imageAdapter = ImagesAdapter(selectedImageUris) { imageUri, position ->
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putStringArrayListExtra("image_urls", ArrayList(selectedImageUris))
                putExtra("image_position", position)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            context?.startActivity(intent)
        }
        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TrophyAddFragment().apply {
            }
    }
}