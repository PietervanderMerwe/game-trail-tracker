package com.epilogs.game_trail_tracker.fragments.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.adapters.LocationAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.fragments.extension.ImageDialogFragment
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImagePickerUtil
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

class AnimalAddFragment : Fragment() {
    private val viewModel: AnimalViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val pickImagesRequestCode = 100
    private val selectedImageUris = mutableListOf<String>()
    private lateinit var imageAdapter: ImagesAdapter
    private var locationId :Int? = 0;
    private var weaponId :Int? = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_animal_add, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextSpecieName: EditText = view.findViewById(R.id.editTextSpecieName)
        val editTextDate : EditText = view.findViewById(R.id.editTextDate)
        val editTextWeight : EditText = view.findViewById(R.id.editTextWeight)
        val editTextMeasurement : EditText = view.findViewById(R.id.editTextMeasurement)
        val spinnerLocation: Spinner = view.findViewById(R.id.spinnerLocation)
        val spinnerWeapon: Spinner = view.findViewById(R.id.spinnerWeapon)
        val buttonSelectAnimalImages: Button = view.findViewById(R.id.buttonSelectAnimalImages)
        val buttonSaveAnimal: Button = view.findViewById(R.id.buttonSaveAnimal)
        val dateConverter = DateConverter()

        editTextDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                editTextDate.setText(selectedDate)
            }
        }

        buttonSelectAnimalImages.setOnClickListener {
            showImagePicker()
        }

        val locations = mutableListOf<Location>()
        val locationAdapter = LocationAdapter(requireContext(), locations)
        spinnerLocation.adapter = locationAdapter

        viewModel.getAllLocations().observe(viewLifecycleOwner) { newLocations  ->
            val modifiedLocations = mutableListOf<Location>().apply {
                add(Location(null, "None",false, null, null,"", mutableListOf<String>())) // Assuming Location is a data class that can handle this
                addAll(newLocations)
            }
            locationAdapter.clear()
            locationAdapter.addAll(modifiedLocations )
            locationAdapter.notifyDataSetChanged()
        }

        val weapons = mutableListOf<Weapon>()
        val weaponAdapter = WeaponAdapter(requireContext(), weapons)
        spinnerWeapon.adapter = weaponAdapter

        viewModel.getAllWeapons().observe(viewLifecycleOwner) { newWeapons  ->
            val modifiedWeapons = mutableListOf<Weapon>().apply {
                add(Weapon(null, "None", "", mutableListOf<String>())) // Similarly, assuming Weapon is a data class
                addAll(newWeapons)
            }
            weaponAdapter.clear()
            weaponAdapter.addAll(modifiedWeapons)
            weaponAdapter.notifyDataSetChanged()
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

        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesAnimalRecyclerView)
        imageAdapter = ImagesAdapter(selectedImageUris) { imageUri ->
            val dialog = ImageDialogFragment.newInstance(imageUri)
            dialog.show(childFragmentManager, "viewImage")
        }
        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedLocation = parent.getItemAtPosition(position) as Location
                locationId = selectedLocation.id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected if needed
            }
        }

        spinnerWeapon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedWeapon = parent.getItemAtPosition(position) as Weapon
                weaponId = selectedWeapon.id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected if needed
            }
        }

        buttonSaveAnimal.setOnClickListener {
            val name = editTextSpecieName.text.toString()
            val weightString  = editTextWeight.text.toString()
            val measurementString  = editTextMeasurement.text.toString()
            val dateString = editTextDate.text.toString()

            val date = dateConverter.parseDate(dateString)
            val notes = "Some notes"
            val imagePaths = selectedImageUris
            val weight = weightString.toDoubleOrNull() ?: 0.0
            val measurement = measurementString.toDoubleOrNull() ?: 0.0
            val animal = Animal(
                name = name,
                weight = weight,
                measurement = measurement,
                harvestDate = date,
                notes = notes,
                locationId = locationId,
                weaponId = weaponId,
                imagePaths = imagePaths
            )

            viewModel.insertAnimal(animal)
        }

        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                editTextSpecieName.text.clear()
                editTextDate.text.clear()
                editTextWeight.text.clear()
                editTextMeasurement.text.clear()
                viewModel.resetInsertionSuccess()
                imageAdapter.clearImages()
                val checkMarkImageView: ImageView = view.findViewById(R.id.checkMarkAnimalAdd)
                checkMarkImageView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    checkMarkImageView.visibility = View.GONE
                }, 3000)
            }
        })
    }

    private fun showImagePicker() {
        ImagePickerUtil.openImagePicker(this, pickImagesRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImagesRequestCode && resultCode == Activity.RESULT_OK) {
            val imagesUris = ImagePickerUtil.extractSelectedImages(data)
            selectedImageUris.clear()
            selectedImageUris.addAll(imagesUris.map { it.toString() })
            imageAdapter.updateImages(selectedImageUris)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnimalAddFragment().apply {
            }
    }
}