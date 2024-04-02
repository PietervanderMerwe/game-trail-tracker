package com.epilogs.game_trail_tracker.fragments.hunt

import android.content.Intent
import android.net.Uri
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Hunt
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
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var imageAdapter: ImagesAdapter
    private val selectedImageUris = mutableListOf<String>()
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            val imagesUris = uris.map(Uri::toString)
            selectedImageUris.clear()
            selectedImageUris.addAll(imagesUris)
            imageAdapter.updateImages(selectedImageUris)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hunt_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
    }

    private fun setupUI(view: View) {
        val editTextName = view.findViewById<EditText>(R.id.editTextName)
        val editTextStartDate = view.findViewById<EditText>(R.id.editTextStartDate)
        val editTextEndDate = view.findViewById<EditText>(R.id.editTextEndDate)
        val dateConverter = DateConverter()

        setupStartDatePicker(editTextStartDate)
        setupEndDatePicker(editTextEndDate)

        view.findViewById<Button>(R.id.buttonSelectLocationImages).setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        setupImagesRecyclerView(view)
        setupSaveButton(view, editTextName, editTextStartDate, editTextEndDate, dateConverter)
        setupObserveInsertion(view, editTextName, editTextStartDate, editTextEndDate)
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
        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesLocationRecyclerView)
        imageAdapter = ImagesAdapter(selectedImageUris) { imageUri, position ->
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putStringArrayListExtra("image_urls", ArrayList(selectedImageUris))
                putExtra("image_position", position)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            context?.startActivity(intent)
        }
        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupSaveButton(view: View, editTextName: EditText,
                                editTextStartDate: EditText, editTextEndDate: EditText, dateConverter: DateConverter) {
        view.findViewById<Button>(R.id.buttonSaveLocation).setOnClickListener {
            val name = editTextName.text.toString()
            val startDate = dateConverter.parseDate(editTextStartDate.text.toString())
            val endDate = dateConverter.parseDate(editTextEndDate.text.toString())
            val location = Hunt(name = name, startDate = startDate,
                endDate = endDate, imagePaths = selectedImageUris)
            viewModel.insertHunt(location)
        }
    }

    private fun setupObserveInsertion(view: View, editTextName: EditText,
                                      editTextStartDate: EditText, editTextEndDate: EditText){
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                editTextName.text.clear()
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

                viewModel.insertionId.observe(viewLifecycleOwner, Observer { id ->
                    id?.let {
                        val action = HuntAddFragmentDirections.actionHuntAddFragmentToHuntViewDetailFragment(
                            id.toInt()
                        )
                        findNavController().navigate(action)
                        viewModel.resetInsertionId()
                    }
                })
            }
        })
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HuntAddFragment().apply {
            }
    }
}