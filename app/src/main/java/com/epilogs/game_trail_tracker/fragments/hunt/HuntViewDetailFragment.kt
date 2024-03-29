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
import com.epilogs.game_trail_tracker.databinding.FragmentHuntBinding
import com.epilogs.game_trail_tracker.databinding.FragmentHuntViewDetailBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HuntViewDetailFragment : Fragment() {

    private var locationId: Int? = null
    private val viewModel: HuntViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentLocation: Location? = null
    private lateinit var binding: FragmentHuntViewDetailBinding
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            locationId = it.getInt("id")
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
        binding = FragmentHuntViewDetailBinding.bind(view)

        binding.backButtonLocationViewDetail.setOnClickListener {
            findNavController().navigateUp()
        }

        disableAllText()

        viewModel.getLocationById(locationId!!).observe(viewLifecycleOwner, Observer { location ->
            currentLocation = location
            binding.editTextNameViewDetail.setText(location?.name)
            binding.editTextStartDateViewDetail.setText(location?.startDate?.let { dateFormat.format(it) } ?: "N/A")
            binding.editTextEndDateViewDetail.setText(location?.endDate?.let { dateFormat.format(it) } ?: "N/A")

            imageAdapter = ImagesAdapter(location?.imagePaths?.toMutableList() ?: mutableListOf()) { imageUrl, position ->
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putStringArrayListExtra("image_urls", ArrayList(location?.imagePaths))
                    putExtra("image_position", position)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                context?.startActivity(intent)
            }

            binding.imagesLocationRecyclerViewDetail.adapter = imageAdapter
            binding.imagesLocationRecyclerViewDetail.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })

        binding.buttonEditLocation.setOnClickListener {
            enableAllText()
        }

        binding.buttonSaveLocation.setOnClickListener {
            val dateConverter = DateConverter()
            currentLocation?.let { location ->

                location.name = binding.editTextNameViewDetail.text.toString()
                location.startDate = dateConverter.parseDate(binding.editTextStartDateViewDetail.text.toString())
                location.endDate = dateConverter.parseDate(binding.editTextEndDateViewDetail.text.toString())

                viewModel.updateLocation(location)
            }
            disableAllText()
        }

        binding.buttonCancelLocation.setOnClickListener {
            disableAllText()
        }

        binding.buttonDeleteLocation.setOnClickListener {
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
        disableEditText(binding.textInputLayoutNameViewDetail)
        disableEditText(binding.textInputLayoutStartDateViewDetail)
        disableEditText(binding.textInputLayoutEndDateViewDetail)

        binding.buttonEditLocation.visibility = View.VISIBLE
        binding.buttonDeleteLocation.visibility = View.VISIBLE
        binding.buttonSaveLocation.visibility = View.GONE
        binding.buttonCancelLocation.visibility = View.GONE

        binding.editTextStartDateViewDetail.setOnClickListener(null)
        binding.editTextEndDateViewDetail.setOnClickListener(null)
    }

    private fun enableAllText() {
        enableEditText(binding.textInputLayoutNameViewDetail)
        enableEditText(binding.textInputLayoutStartDateViewDetail)
        enableEditText(binding.textInputLayoutEndDateViewDetail)

        binding.buttonEditLocation.visibility = View.GONE
        binding.buttonDeleteLocation.visibility = View.GONE
        binding.buttonSaveLocation.visibility = View.VISIBLE
        binding.buttonCancelLocation.visibility = View.VISIBLE

        setupStartDatePicker(binding.editTextStartDateViewDetail)
        setupEndDatePicker(binding.editTextEndDateViewDetail)
    }

    private fun setupStartDatePicker(editText: EditText) {
        editText.setOnClickListener {
            showDatePickerDialog(requireContext(), { selectedDate ->
                editText.setText(selectedDate)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                startDate = Calendar.getInstance().apply {
                    time = formatter.parse(selectedDate) ?: return@apply
                }
            }, maxDate = endDate)
        }
    }

    private fun setupEndDatePicker(editText: EditText) {
        editText.setOnClickListener {
            showDatePickerDialog(requireContext(), { selectedDate ->
                editText.setText(selectedDate)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                endDate = Calendar.getInstance().apply {
                    time = formatter.parse(selectedDate) ?: return@apply
                }
            }, minDate = startDate)
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