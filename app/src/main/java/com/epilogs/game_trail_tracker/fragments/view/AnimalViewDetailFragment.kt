package com.epilogs.game_trail_tracker.fragments.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Location
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
        val weaponViewDetail: EditText = view.findViewById(R.id.WeaponViewDetail)
        val imagesRecyclerView =
            view.findViewById<RecyclerView>(R.id.imagesAnimalRecyclerViewViewDetail)
        val deleteButton: Button = view.findViewById(R.id.button_delete_animal)
        val editButton: Button = view.findViewById(R.id.button_edit_animal)
        val saveButton: Button = view.findViewById(R.id.button_save_animal)

        disableEditText(specieName)
        disableEditText(date)
        disableEditText(weight)
        disableEditText(measurement)

        date.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                date.setText(selectedDate)
            }
        }

        editButton.setOnClickListener {
            enableEditText(specieName)
            enableEditText(date)
            enableEditText(weight)
            enableEditText(measurement)

            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
            saveButton.visibility = View.VISIBLE
        }


        animalViewModel.getAnimalById(animalId!!).observe(viewLifecycleOwner, Observer { animal ->
            currentAnimal = animal
            specieName.setText(animal?.name)
            date.setText(animal?.harvestDate?.let { dateFormat.format(it) } ?: "N/A")
            weight.setText(animal?.weight?.toString())
            measurement.setText(animal?.measurement?.toString())

            animal?.weaponId?.let { weaponId ->
                weaponViewModel.getWeaponById(weaponId)
                    .observe(viewLifecycleOwner, Observer { weapon ->
                        weaponViewDetail.setText(weapon?.name)
                    })
            }

            animal?.locationId?.let { animalId ->
                locationViewModel.getLocationById(animalId)
                    .observe(viewLifecycleOwner, Observer { location ->
                        locationViewDetail.setText(location?.name)
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

                animalViewModel.updateAnimal(animal)
            }

            disableEditText(specieName)
            disableEditText(date)
            disableEditText(weight)
            disableEditText(measurement)

            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
            saveButton.visibility = View.GONE
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
        editText.isClickable = true
        editText.isCursorVisible = false
    }

    private fun enableEditText(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.isClickable = true
        editText.isCursorVisible = true
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