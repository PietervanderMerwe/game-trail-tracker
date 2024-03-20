package com.epilogs.game_trail_tracker.fragments.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.adapters.LocationAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AnimalViewDetailFragment : Fragment() {

    private var animalId: Int? = null
    private val animalViewModel: AnimalViewModel by viewModels()
    private val weaponViewModel: WeaponViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentAnimal: Animal? = null
    private var locationId = 0;
    private var weaponId = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            animalId = it.getInt("animalId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_animal_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val specieName: EditText = view.findViewById(R.id.editTextSpecieNameViewDetail)
        val date: EditText = view.findViewById(R.id.editTextDateViewDetail)
        val weight: EditText = view.findViewById(R.id.editTextWeightViewDetail)
        val measurement: EditText = view.findViewById(R.id.editTextMeasurementViewDetail)
        val locationViewDetail: EditText = view.findViewById(R.id.LocationViewDetail)
        val locationSpinnerViewDetail: Spinner = view.findViewById(R.id.spinnerLocationViewDetail)
        val weaponViewDetail: EditText = view.findViewById(R.id.WeaponViewDetail)
        val weaponSpinnerViewDetail: Spinner = view.findViewById(R.id.spinnerWeaponViewDetail)
        val imagesRecyclerView =
            view.findViewById<RecyclerView>(R.id.imagesAnimalRecyclerViewViewDetail)
        val deleteButton: Button = view.findViewById(R.id.button_delete_animal)
        val editButton: Button = view.findViewById(R.id.button_edit_animal)
        val saveButton: Button = view.findViewById(R.id.button_save_animal)
        val cancelButton: Button = view.findViewById(R.id.button_cancel_animal)
        val locationLayout : LinearLayout = view.findViewById(R.id.LocationLayoutViewDetail)
        val weaponLayout : LinearLayout = view.findViewById(R.id.WeaponLayoutViewDetail)
        val viewLocationButton: Button = view.findViewById(R.id.button_view_location)
        val viewWeaponButton: Button = view.findViewById(R.id.button_view_weapon)
        val backButton: ImageView = view.findViewById(R.id.backButtonAnimalViewDetail)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewLocationButton.setOnClickListener {
            val action = AnimalViewDetailFragmentDirections.actionAnimalViewDetailFragment2ToLocationViewDetailFragment2(locationId)
            findNavController().navigate(action)
        }

        viewWeaponButton.setOnClickListener {
            val action = AnimalViewDetailFragmentDirections.actionAnimalViewDetailFragment2ToWeaponViewDetailFragment2(weaponId)
            findNavController().navigate(action)
        }

        disableEditText(specieName)
        disableEditText(date)
        disableEditText(weight)
        disableEditText(measurement)
        disableEditText(locationViewDetail)
        disableEditText(weaponViewDetail)

        date.setOnClickListener(null)

        editButton.setOnClickListener {
            enableEditText(specieName)
            enableEditText(date)
            enableEditText(weight)
            enableEditText(measurement)

            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
            locationLayout.visibility = View.GONE
            weaponLayout.visibility = View.GONE
            locationSpinnerViewDetail.visibility = View.VISIBLE
            weaponSpinnerViewDetail.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE

            val locations = mutableListOf<Location>()
            val locationAdapter = LocationAdapter(requireContext(), locations)
            locationSpinnerViewDetail.adapter = locationAdapter

            locationViewModel.getAllLocations().observe(viewLifecycleOwner) { newLocations  ->
                locationAdapter.clear()
                locationAdapter.addAll(newLocations )
                locationAdapter.notifyDataSetChanged()
            }

            val weapons = mutableListOf<Weapon>()
            val weaponAdapter = WeaponAdapter(requireContext(), weapons)
            weaponSpinnerViewDetail.adapter = weaponAdapter

            weaponViewModel.getAllWeapons().observe(viewLifecycleOwner) { newWeapons  ->
                weaponAdapter.clear()
                weaponAdapter.addAll(newWeapons)
                weaponAdapter.notifyDataSetChanged()
            }

            locationSpinnerViewDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedLocation = parent.getItemAtPosition(position) as Location
                    locationViewDetail.setText(selectedLocation.name)
                    locationId = selectedLocation.id!!
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle case when nothing is selected if needed
                }
            }

            weaponSpinnerViewDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedWeapon = parent.getItemAtPosition(position) as Weapon
                    weaponViewDetail.setText(selectedWeapon.name)
                    weaponId = selectedWeapon.id!!
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle case when nothing is selected if needed
                }
            }

            date.setOnClickListener {
                showDatePickerDialog(requireContext()) { selectedDate ->
                    date.setText(selectedDate)
                }
            }
        }

        animalViewModel.getAnimalById(animalId!!).observe(viewLifecycleOwner, Observer { animal ->
            currentAnimal = animal
            specieName.setText(animal?.name)
            date.setText(animal?.harvestDate?.let { dateFormat.format(it) } ?: "N/A")
            weight.setText(animal?.weight?.toString())
            measurement.setText(animal?.measurement?.toString())

            animal?.weaponId?.let { id ->
                weaponViewModel.getWeaponById(id)
                    .observe(viewLifecycleOwner, Observer { weapon ->
                        weaponViewDetail.setText(weapon?.name)
                        weaponId = id
                    })
            }

            animal?.locationId?.let { id ->
                locationViewModel.getLocationById(id)
                    .observe(viewLifecycleOwner, Observer { location ->
                        locationViewDetail.setText(location?.name)
                        locationId = id
                    })
            }

            imageAdapter = ImagesAdapter(mutableListOf())
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            animal?.imagePaths?.let { imageUrls ->
                imageAdapter.updateImages(imageUrls)
            }
        })

        saveButton.setOnClickListener {
            val dateConverter = DateConverter()
            currentAnimal?.let { animal ->

                animal.name = specieName.text.toString()
                animal.harvestDate = dateConverter.parseDate(date.text.toString())
                animal.measurement = measurement.text.toString().toDoubleOrNull() ?: 0.0
                animal.weight = weight.text.toString().toDoubleOrNull() ?: 0.0
                animal.locationId = locationId
                animal.weaponId = weaponId
                animalViewModel.updateAnimal(animal)
            }

            disableEditText(specieName)
            disableEditText(date)
            disableEditText(weight)
            disableEditText(measurement)
            date.setOnClickListener(null)

            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
            locationLayout.visibility = View.VISIBLE
            weaponLayout.visibility = View.VISIBLE
            locationSpinnerViewDetail.visibility = View.GONE
            weaponSpinnerViewDetail.visibility = View.GONE
            saveButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
        }

        cancelButton.setOnClickListener {
            disableEditText(specieName)
            disableEditText(date)
            disableEditText(weight)
            disableEditText(measurement)
            date.setOnClickListener(null)

            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
            locationLayout.visibility = View.VISIBLE
            weaponLayout.visibility = View.VISIBLE
            locationSpinnerViewDetail.visibility = View.GONE
            weaponSpinnerViewDetail.visibility = View.GONE
            saveButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this animal?")
            .setPositiveButton("Delete") { dialog, which ->
                animalViewModel.deleteAnimal(currentAnimal!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isClickable = false
        editText.isCursorVisible = false
        editText.background = null
    }

    private fun enableEditText(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.isClickable = true
        editText.isCursorVisible = true
        editText.setBackgroundResource(android.R.drawable.edit_text)
    }

    companion object {
        @JvmStatic
        fun newInstance(animalId: Int) =
            AnimalViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("animalId", animalId)
                }
            }
    }
}