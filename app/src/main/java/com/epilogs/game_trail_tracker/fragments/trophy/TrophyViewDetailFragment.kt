package com.epilogs.game_trail_tracker.fragments.trophy

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
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
import androidx.core.content.ContextCompat
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
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Locale

class TrophyViewDetailFragment : Fragment() {

    private var animalId: Int? = null
    private val animalViewModel: AnimalViewModel by viewModels()
    private val weaponViewModel: WeaponViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentAnimal: Animal? = null
    private var locationId: Int? = 0;
    private var weaponId: Int? = 0;
    private lateinit var specieNameLayout: TextInputLayout
    private lateinit var weightLayout: TextInputLayout
    private lateinit var measurementLayout: TextInputLayout
    private lateinit var dateLayout: TextInputLayout
    private lateinit var deleteButton: Button
    private lateinit var editButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var date: EditText
    private lateinit var locationLayout: LinearLayout
    private lateinit var weaponLayout: LinearLayout
    private lateinit var locationSpinnerViewDetail: Spinner
    private lateinit var weaponSpinnerViewDetail: Spinner

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
        return inflater.inflate(R.layout.fragment_trophy_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        specieNameLayout = view.findViewById(R.id.textInputLayoutSpecieNameViewDetail)
        weightLayout = view.findViewById(R.id.textInputLayoutWeightViewDetail)
        measurementLayout = view.findViewById(R.id.textInputLayoutMeasurementViewDetail)
        dateLayout = view.findViewById(R.id.textInputLayoutDateViewDetail)
        val specieName: EditText = view.findViewById(R.id.editTextSpecieNameViewDetail)
        date = view.findViewById(R.id.editTextDateViewDetail)
        val weight: EditText = view.findViewById(R.id.editTextWeightViewDetail)
        val measurement: EditText = view.findViewById(R.id.editTextMeasurementViewDetail)
        val locationViewDetail: EditText = view.findViewById(R.id.LocationViewDetail)
        locationSpinnerViewDetail = view.findViewById(R.id.spinnerLocationViewDetail)
        val weaponViewDetail: EditText = view.findViewById(R.id.WeaponViewDetail)
        weaponSpinnerViewDetail = view.findViewById(R.id.spinnerWeaponViewDetail)
        val imagesRecyclerView =
            view.findViewById<RecyclerView>(R.id.imagesAnimalRecyclerViewViewDetail)
        deleteButton = view.findViewById(R.id.button_delete_animal)
        editButton = view.findViewById(R.id.button_edit_animal)
        saveButton = view.findViewById(R.id.button_save_animal)
        cancelButton = view.findViewById(R.id.button_cancel_animal)
        locationLayout = view.findViewById(R.id.LocationLayoutViewDetail)
        weaponLayout = view.findViewById(R.id.WeaponLayoutViewDetail)
        val viewLocationButton: Button = view.findViewById(R.id.button_view_location)
        val viewWeaponButton: Button = view.findViewById(R.id.button_view_weapon)
        val backButton: ImageView = view.findViewById(R.id.backButtonAnimalViewDetail)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewLocationButton.setOnClickListener {
            val action =
                TrophyViewDetailFragmentDirections.actionAnimalViewDetailFragment2ToLocationViewDetailFragment2(
                    locationId!!
                )
            findNavController().navigate(action)
        }

        viewWeaponButton.setOnClickListener {
            val action =
                TrophyViewDetailFragmentDirections.actionAnimalViewDetailFragment2ToWeaponViewDetailFragment2(
                    weaponId!!
                )
            findNavController().navigate(action)
        }

        disableAllText()

        editButton.setOnClickListener {
            enableAllText()

            val locations = mutableListOf<Location>()
            val locationAdapter = LocationAdapter(requireContext(), locations)
            locationSpinnerViewDetail.adapter = locationAdapter

            locationViewModel.getAllLocations().observe(viewLifecycleOwner) { newLocations ->
                val modifiedLocations = mutableListOf<Location>().apply {
                    add(
                        Location(
                            null,
                            "None",
                            false,
                            null,
                            null,
                            "",
                            mutableListOf<String>()
                        )
                    )
                    addAll(newLocations)
                }
                locationAdapter.clear()
                locationAdapter.addAll(modifiedLocations)
                locationAdapter.notifyDataSetChanged()

                val defaultPosition = modifiedLocations.indexOfFirst { it.id == locationId }

                if (defaultPosition >= 0) {
                    locationSpinnerViewDetail.setSelection(defaultPosition)
                }
            }

            val weapons = mutableListOf<Weapon>()
            val weaponAdapter = WeaponAdapter(requireContext(), weapons)
            weaponSpinnerViewDetail.adapter = weaponAdapter

            weaponViewModel.getAllWeapons().observe(viewLifecycleOwner) { newWeapons ->
                val modifiedWeapons = mutableListOf<Weapon>().apply {
                    add(Weapon(null, "None", "", mutableListOf<String>()))
                    addAll(newWeapons)
                }
                weaponAdapter.clear()
                weaponAdapter.addAll(modifiedWeapons)
                weaponAdapter.notifyDataSetChanged()

                val defaultPosition = modifiedWeapons.indexOfFirst { it.id == weaponId }

                if (defaultPosition >= 0) {
                    weaponSpinnerViewDetail.setSelection(defaultPosition)
                }
            }

            locationSpinnerViewDetail.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val selectedLocation = parent.getItemAtPosition(position) as Location
                        locationViewDetail.setText(selectedLocation.name)
                        locationId = selectedLocation.id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle case when nothing is selected if needed
                    }
                }

            weaponSpinnerViewDetail.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val selectedWeapon = parent.getItemAtPosition(position) as Weapon
                        weaponViewDetail.setText(selectedWeapon.name)
                        weaponId = selectedWeapon.id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle case when nothing is selected if needed
                    }
                }
        }

        animalViewModel.getAnimalById(animalId!!).observe(viewLifecycleOwner, Observer { animal ->
            currentAnimal = animal
            specieName.setText(animal?.name)
            date.setText(animal?.harvestDate?.let { dateFormat.format(it) } ?: "N/A")
            weight.setText(animal?.weight?.toString())
            measurement.setText(animal?.measurement?.toString())

            if (animal?.weaponId == null) {
                weaponLayout.visibility = View.GONE
            } else {
                animal.weaponId?.let { id ->
                    weaponViewModel.getWeaponById(id)
                        .observe(viewLifecycleOwner, Observer { weapon ->
                            weaponViewDetail.setText(weapon?.name)
                            weaponId = id
                        })
                }
                weaponLayout.visibility = View.VISIBLE
            }

            if (animal?.locationId == null) {
                locationLayout.visibility = View.GONE
            } else {
                animal.locationId?.let { id ->
                    locationViewModel.getLocationById(id)
                        .observe(viewLifecycleOwner, Observer { location ->
                            locationViewDetail.setText(location?.name)
                            locationId = id
                        })
                }
                locationLayout.visibility = View.VISIBLE
            }

            imageAdapter = ImagesAdapter(animal?.imagePaths?.toMutableList() ?: mutableListOf()) { imageUrl, position ->
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putStringArrayListExtra("image_urls", ArrayList(animal?.imagePaths))
                    putExtra("image_position", position)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                context?.startActivity(intent)
            }

            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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

            disableAllText()
        }

        cancelButton.setOnClickListener {
            disableAllText()
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

    private fun disableAllText() {
        disableEditText(specieNameLayout)
        disableEditText(weightLayout)
        disableEditText(measurementLayout)
        disableEditText(dateLayout)

        editButton.visibility = View.VISIBLE
        deleteButton.visibility = View.VISIBLE
        locationLayout.visibility = View.VISIBLE
        weaponLayout.visibility = View.VISIBLE
        locationSpinnerViewDetail.visibility = View.GONE
        weaponSpinnerViewDetail.visibility = View.GONE
        saveButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        date.setOnClickListener(null)
    }

    private fun enableAllText() {
        enableEditText(specieNameLayout)
        enableEditText(weightLayout)
        enableEditText(measurementLayout)
        enableEditText(dateLayout)

        editButton.visibility = View.GONE
        deleteButton.visibility = View.GONE
        locationLayout.visibility = View.GONE
        weaponLayout.visibility = View.GONE
        locationSpinnerViewDetail.visibility = View.VISIBLE
        weaponSpinnerViewDetail.visibility = View.VISIBLE
        saveButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE

        date.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                date.setText(selectedDate)
            }
        }
    }

    private fun disableEditText(textInputLayout: TextInputLayout) {
        textInputLayout.isEnabled = false
        textInputLayout.editText?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black_disabled
            )
        )
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black_disabled
            )
        )
        textInputLayout.editText?.apply {
            isClickable = false
            isFocusable = false
            isCursorVisible = false
        }
    }

    private fun enableEditText(textInputLayout: TextInputLayout) {
        textInputLayout.isEnabled = true
        textInputLayout.editText?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black
            )
        )
        textInputLayout.defaultHintTextColor =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_black))
        textInputLayout.editText?.apply {
            isClickable = true
            isFocusableInTouchMode = true
            isCursorVisible = true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(animalId: Int) =
            TrophyViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("animalId", animalId)
                }
            }
    }
}