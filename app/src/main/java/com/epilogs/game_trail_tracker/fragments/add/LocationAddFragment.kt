package com.epilogs.game_trail_tracker.fragments.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImagePickerUtil
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel

class LocationAddFragment : Fragment() {
    private val viewModel: LocationViewModel by viewModels()
    private val pickImagesRequestCode = 100
    private val selectedImageUris = mutableListOf<String>()
    private lateinit var imageAdapter: ImagesAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val checkBoxIsContinues = view.findViewById<CheckBox>(R.id.checkBoxIsContinues)
        val editTextStartDate = view.findViewById<EditText>(R.id.editTextStartDate)
        val editTextEndDate = view.findViewById<EditText>(R.id.editTextEndDate)
        val buttonSelectLocationImages: Button = view.findViewById(R.id.buttonSelectLocationImages)
        val buttonSaveLocation: Button = view.findViewById(R.id.buttonSaveLocation)
        val dateConverter = DateConverter()

        editTextStartDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                editTextStartDate.setText(selectedDate)
            }
        }

        editTextEndDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                editTextEndDate.setText(selectedDate)
            }
        }

        checkBoxIsContinues.setOnCheckedChangeListener { _, isChecked ->
            editTextStartDate.isEnabled = !isChecked
            editTextEndDate.isEnabled = !isChecked

            if (isChecked) {
                editTextStartDate.text.clear()
                editTextEndDate.text.clear()
            }
        }

        buttonSelectLocationImages.setOnClickListener {
            showImagePicker()
        }

        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesLocationRecyclerView)
        imageAdapter = ImagesAdapter(mutableListOf())
        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        buttonSaveLocation.setOnClickListener {
            val name = editTextName.text.toString()
            val isContinues = checkBoxIsContinues.isChecked
            val startDateString = editTextStartDate.text.toString()
            val endDateString = editTextEndDate.text.toString()

            val startDate = dateConverter.parseDate(startDateString)
            val endDate = dateConverter.parseDate(endDateString)
            val notes = "Some notes"
            val imagePaths = selectedImageUris

            val location = Location(
                name = name,
                isContinues = isContinues,
                startDate = startDate,
                endDate = endDate,
                notes = notes,
                imagePaths = imagePaths
            )

            viewModel.insertLocation(location)
        }

        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                editTextName.text.clear()
                checkBoxIsContinues.isChecked = false
                editTextStartDate.text.clear()
                editTextEndDate.text.clear()
                viewModel.resetInsertionSuccess()
                imageAdapter.clearImages()
                val checkMarkImageView: ImageView = view.findViewById(R.id.checkMarkLocationAdd)
                checkMarkImageView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    checkMarkImageView.visibility = View.GONE
                }, 3000)
                sharedViewModel.notifyLocationsUpdated()
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
        fun newInstance(param1: String, param2: String) =
            LocationAddFragment().apply {
            }
    }
}