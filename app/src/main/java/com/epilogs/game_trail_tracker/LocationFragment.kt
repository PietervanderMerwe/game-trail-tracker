package com.epilogs.game_trail_tracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.epilogs.game_trail_tracker.utils.ImagePickerUtil
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LocationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val pickImagesRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val checkBoxIsContinues = view.findViewById<CheckBox>(R.id.checkBoxIsContinues)
        val editTextStartDate = view.findViewById<EditText>(R.id.editTextStartDate)
        val editTextEndDate = view.findViewById<EditText>(R.id.editTextEndDate)
        val buttonSelectImages: Button = view.findViewById(R.id.buttonSelectImages)

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

        buttonSelectImages.setOnClickListener {
            showImagePicker()
        }
    }

    private fun showImagePicker() {
        ImagePickerUtil.openImagePicker(this, pickImagesRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImagesRequestCode && resultCode == Activity.RESULT_OK) {
            val imagesUris = ImagePickerUtil.extractSelectedImages(data)
            // Handle selected images
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}