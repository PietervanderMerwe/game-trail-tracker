package com.epilogs.game_trail_tracker.fragments.hunt

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Locale

class HuntViewDetailFragment : Fragment() {

    private var locationId: Int? = null
    private val viewModel: HuntViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentLocation: Location? = null
    private lateinit var nameLayout: TextInputLayout
    private lateinit var startDateLayout: TextInputLayout
    private lateinit var endDateLayout: TextInputLayout
    private lateinit var deleteButton: Button
    private lateinit var editButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var checkBoxIsContinuous: CheckBox
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            locationId = it.getInt("locationId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        nameLayout = view.findViewById(R.id.textInputLayoutNameViewDetail)
        startDateLayout = view.findViewById(R.id.textInputLayoutStartDateViewDetail)
        endDateLayout = view.findViewById(R.id.textInputLayoutEndDateViewDetail)
        val name: EditText = view.findViewById(R.id.editTextNameViewDetail)
        startDate = view.findViewById(R.id.editTextStartDateViewDetail)
        endDate = view.findViewById(R.id.editTextEndDateViewDetail)
        checkBoxIsContinuous = view.findViewById(R.id.checkBoxIsContinuesViewDetail)
        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesLocationRecyclerViewDetail)
        deleteButton = view.findViewById(R.id.button_delete_location)
        editButton = view.findViewById(R.id.button_edit_location)
        saveButton = view.findViewById(R.id.button_save_location)
        cancelButton = view.findViewById(R.id.button_cancel_location)
        val backButton: ImageView = view.findViewById(R.id.backButtonLocationViewDetail)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        disableAllText()

        viewModel.getLocationById(locationId!!).observe(viewLifecycleOwner, Observer { location ->
            currentLocation = location
            name.setText(location?.name)
            startDate.setText(location?.startDate?.let { dateFormat.format(it) } ?: "N/A")
            endDate.setText(location?.endDate?.let { dateFormat.format(it) } ?: "N/A")

            imageAdapter = ImagesAdapter(location?.imagePaths?.toMutableList() ?: mutableListOf()) { imageUrl, position ->
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putStringArrayListExtra("image_urls", ArrayList(location?.imagePaths))
                    putExtra("image_position", position)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                context?.startActivity(intent)
            }

            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })

        checkBoxIsContinuous.setOnCheckedChangeListener { _, isChecked ->
            startDate.isEnabled = !isChecked
            endDate.isEnabled = !isChecked

            if (isChecked) {
                startDate.text.clear()
                endDate.text.clear()
            }
        }

        editButton.setOnClickListener {
            enableAllText()
        }

        saveButton.setOnClickListener {
            val dateConverter = DateConverter()
            currentLocation?.let { location ->

                location.name = name.text.toString()
                location.startDate = dateConverter.parseDate(startDate.text.toString())
                location.endDate = dateConverter.parseDate(endDate.text.toString())

                viewModel.updateLocation(location)
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
            .setMessage("Are you sure you want to delete this location?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteLocation(currentLocation!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun disableAllText() {
        disableEditText(nameLayout)
        disableEditText(startDateLayout)
        disableEditText(endDateLayout)

        checkBoxIsContinuous.isEnabled = false

        editButton.visibility = View.VISIBLE
        deleteButton.visibility = View.VISIBLE
        saveButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        startDate.setOnClickListener(null)
        endDate.setOnClickListener(null)
    }

    private fun enableAllText() {
        enableEditText(nameLayout)
        enableEditText(startDateLayout)
        enableEditText(endDateLayout)

        checkBoxIsContinuous.isEnabled = true

        editButton.visibility = View.GONE
        deleteButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE

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
    }

    private fun disableEditText(textInputLayout: TextInputLayout) {
        textInputLayout.isEnabled = false
        textInputLayout.editText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))
        textInputLayout.editText?.apply {
            isClickable = false
            isFocusable = false
            isCursorVisible = false
        }
    }

    private fun enableEditText(textInputLayout: TextInputLayout) {
        textInputLayout.isEnabled = true
        textInputLayout.editText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_black))
        textInputLayout.editText?.apply {
            isClickable = true
            isFocusableInTouchMode = true
            isCursorVisible = true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(locationId: Int) =
            HuntViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("locationId", locationId)
                }
            }
    }
}