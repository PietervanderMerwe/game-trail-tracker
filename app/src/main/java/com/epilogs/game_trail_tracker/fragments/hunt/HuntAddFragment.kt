package com.epilogs.game_trail_tracker.fragments.hunt

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.databinding.FragmentHuntAddBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HuntAddFragment : Fragment() {
    private val viewModel: HuntViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var documentPickerLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var imageAdapter: ImagesAdapter
    private val selectedImageUris = mutableSetOf<String>()
    private val temporaryImageUris = mutableListOf<String>()
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var huntId: Int? = null
    private lateinit var binding: FragmentHuntAddBinding
    private var currentHunt: Hunt? = null

    @SuppressLint("BinderGetCallingInMainThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            huntId = it.getInt("huntId")
        }
        documentPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris != null && uris.isNotEmpty()) {
                temporaryImageUris.clear()
                uris.forEach { uri ->
                    val takeFlags: Int =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    requireActivity().contentResolver.takePersistableUriPermission(uri, takeFlags)
                    temporaryImageUris.add(uri.toString())
                }
                updateSelectedImages()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHuntAddBinding.bind(view)
        setupUI(view)
        imageAdapter.updateImages(selectedImageUris)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_add, container, false)
    }

    private fun updateSelectedImages() {
        selectedImageUris.clear()
        selectedImageUris.addAll(temporaryImageUris)
        imageAdapter.updateImages(selectedImageUris)
    }

    private fun setupUI(view: View) {
        val dateConverter = DateConverter()

        setupStartDatePicker(binding.editTextStartDate)
        setupEndDatePicker(binding.editTextEndDate)

        view.findViewById<Button>(R.id.buttonSelectLocationImages).setOnClickListener {
            documentPickerLauncher.launch(arrayOf("image/*"))
        }

        setupImagesRecyclerView(view)
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
            imageAdapter.updateImages(selectedImageUris)
        }
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

    private fun setupImagesRecyclerView(view: View) {
        imageAdapter = ImagesAdapter(selectedImageUris.toMutableList()) { imageUri, position ->
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putStringArrayListExtra("image_urls", ArrayList(selectedImageUris))
                putExtra("image_position", position)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            context?.startActivity(intent)
        }
        binding.imagesLocationRecyclerView.adapter = imageAdapter
        binding.imagesLocationRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
        imageAdapter.clearImages()
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HuntAddFragment().apply {
            }
    }
}