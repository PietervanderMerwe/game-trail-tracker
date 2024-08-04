package com.epilogs.game_trail_tracker.fragments.hunt

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.databinding.FragmentHuntAddBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HuntAddFragment : Fragment() {
    private val viewModel: HuntViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val selectedImageUris = mutableSetOf<String>()
    private var startDateCalendar: Calendar? = null
    private var endDateCalendar: Calendar? = null
    private var huntId: Int? = null
    private lateinit var binding: FragmentHuntAddBinding
    private var currentHunt: Hunt? = null
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            huntId = it.getInt("huntId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHuntAddBinding.bind(view)
        setupUI()
        imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_add, container, false)
    }

    private fun setupUI() {
        val dateConverter = DateConverter()

        setupDateRangePicker(binding.editTextStartDate, binding.editTextEndDate)

        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
            }
        )

        binding.buttonSelectLocationImages.setOnClickListener {
            imagePickerSetup.pickImages()
        }

        setupImageAdapter(selectedImageUris)
        setupSaveButton(dateConverter)
        setupObserveInsertion()
        if (huntId != 0) {
            setupEditScreen()
            setupObserveUpdate()
        }
    }

    private fun setupEditScreen() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.addHuntText.text = getString(R.string.update_hunt)
        binding.buttonSaveLocation.text = getString(R.string.button_update)
        binding.buttonDeleteWeapon.visibility = View.VISIBLE

        binding.buttonDeleteWeapon.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        viewModel.getHuntById(huntId!!).observe(viewLifecycleOwner) { hunt ->
            currentHunt = hunt
            binding.editTextName.setText(hunt?.name)
            binding.editTextStartDate.setText(hunt?.startDate?.let { dateFormat.format(it) })
            binding.editTextEndDate.setText(hunt?.endDate?.let { dateFormat.format(it) })
            selectedImageUris.addAll(hunt?.imagePaths ?: emptyList())
            imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
        }
    }

    private fun setupDateRangePicker(editTextStart: EditText, editTextEnd: EditText) {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        editTextStart.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()

            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .setCalendarConstraints(constraintsBuilder.build())
                .setTheme(R.style.CustomDatePickerTheme)
                .build()

            dateRangePicker.show(parentFragmentManager, dateRangePicker.toString())

            dateRangePicker.addOnPositiveButtonClickListener { selection ->
                val startMillis = selection.first
                val endMillis = selection.second

                if (startMillis != null && endMillis != null) {
                    val startDate = formatter.format(Date(startMillis))
                    val endDate = formatter.format(Date(endMillis))

                    editTextStart.setText(startDate)
                    editTextEnd.setText(endDate)

                    startDateCalendar = Calendar.getInstance().apply {
                        timeInMillis = startMillis
                    }
                    endDateCalendar = Calendar.getInstance().apply {
                        timeInMillis = endMillis
                    }
                }
            }
        }

        editTextEnd.setOnClickListener {
            editTextStart.performClick()
        }
    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesLocationRecyclerView,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    private fun setupSaveButton(dateConverter: DateConverter) {
        binding.buttonSaveLocation.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val startDate = dateConverter.parseDate(binding.editTextStartDate.text.toString())
            val endDate = dateConverter.parseDate(binding.editTextEndDate.text.toString())
            val location = Hunt(
                name = name, startDate = startDate,
                endDate = endDate, imagePaths = selectedImageUris.toMutableList()
            )
            if (huntId == 0) {
                viewModel.insertHunt(location)
            } else {
                location.id = huntId
                viewModel.updateHunt(location)
            }
        }
    }

    private fun setupObserveInsertion() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                clearScreen()
                showCheckMark()
                viewModel.insertionId.observe(viewLifecycleOwner) { id ->
                    id?.let {
                        val action =
                            HuntAddFragmentDirections.actionHuntAddFragmentToHuntViewDetailFragment(
                                id.toInt()
                            )
                        findNavController().navigate(action)
                        viewModel.resetInsertionId()
                    }
                }
            }
        })
    }

    private fun setupObserveUpdate() {
        viewModel.getUpdateSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) {
                clearScreen()
                findNavController().navigateUp()
            }
        }
    }

    private fun clearScreen() {
        binding.editTextName.text.clear()
        binding.editTextStartDate.text.clear()
        binding.editTextEndDate.text.clear()
        viewModel.resetInsertionSuccess()
        viewModel.resetUpdateSuccess()
        imageAdapterSetup.clearImages()
        binding.buttonDeleteWeapon.visibility = View.GONE
    }
    private fun showCheckMark() {
        view?.findViewById<ImageView>(R.id.checkMarkLocationAdd)?.let { checkMarkImageView ->
            checkMarkImageView.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                checkMarkImageView.visibility = View.GONE
            }, 3000)
        }
        sharedViewModel.notifyWeaponsUpdated()
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this hunt?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteHunt(currentHunt!!)
                val action = HuntAddFragmentDirections.actionHuntAddFragmentToHuntFragment()
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