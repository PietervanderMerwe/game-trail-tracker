package com.epilogs.game_trail_tracker.fragments.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.adapters.LocationViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class LocationViewDetailFragment : Fragment() {

    private var locationId: Int? = null
    private val viewModel: LocationViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            locationId = it.getInt("locationId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val name: EditText = view.findViewById(R.id.editTextNameViewDetail)
        val startDate: EditText = view.findViewById(R.id.editTextStartDateViewDetail)
        val endDate: EditText = view.findViewById(R.id.editTextEndDateViewDetail)
        val checkBoxIsContinuous = view.findViewById<CheckBox>(R.id.checkBoxIsContinuesViewDetail)
        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesLocationRecyclerViewDetail)
        val deleteButton: Button = view.findViewById(R.id.button_delete_location)
        val editButton: Button = view.findViewById(R.id.button_edit_location)
        val saveButton: Button = view.findViewById(R.id.button_save_location)

        val backButton: ImageView = view.findViewById(R.id.backButtonLocationViewDetail)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        disableEditText(name)
        disableEditText(startDate)
        disableEditText(endDate)
        checkBoxIsContinuous.isEnabled = false

        viewModel.getLocationById(locationId!!).observe(viewLifecycleOwner, Observer { location ->
            currentLocation = location
            name.setText(location?.name)
            startDate.setText(location?.startDate?.let { dateFormat.format(it) } ?: "N/A")
            endDate.setText(location?.endDate?.let { dateFormat.format(it) } ?: "N/A")
            checkBoxIsContinuous.isChecked = location?.isContinues!!

            imageAdapter = ImagesAdapter(mutableListOf())
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            location.imagePaths?.let { imageUrls ->
                imageAdapter.updateImages(imageUrls)
            }
        })

        startDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                startDate.setText(selectedDate)
            }
        }

        endDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                endDate.setText(selectedDate)
            }
        }

        checkBoxIsContinuous.setOnCheckedChangeListener { _, isChecked ->
            startDate.isEnabled = !isChecked
            endDate.isEnabled = !isChecked

            if (isChecked) {
                startDate.text.clear()
                endDate.text.clear()
            }
        }

        editButton.setOnClickListener {
            enableEditText(name)
            enableEditText(startDate)
            enableEditText(endDate)
            checkBoxIsContinuous.isEnabled = true

            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
            saveButton.visibility = View.VISIBLE
        }

        saveButton.setOnClickListener {
            val dateConverter = DateConverter()
            currentLocation?.let { location ->

                location.name = name.text.toString()
                location.startDate = dateConverter.parseDate(startDate.text.toString())
                location.endDate = dateConverter.parseDate(endDate.text.toString())
                location.isContinues = checkBoxIsContinuous.isChecked

                viewModel.updateLocation(location)
            }

            disableEditText(name)
            disableEditText(startDate)
            disableEditText(endDate)
            checkBoxIsContinuous.isEnabled = false

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
            .setMessage("Are you sure you want to delete this location?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteLocation(currentLocation!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isClickable = false
        editText.isCursorVisible = false
    }

    private fun enableEditText(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.isClickable = true
        editText.isCursorVisible = true
    }

    companion object {
        @JvmStatic
        fun newInstance(locationId: Int) =
            LocationViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("locationId", locationId)
                }
            }
    }
}