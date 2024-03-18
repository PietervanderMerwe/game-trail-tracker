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
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.adapters.LocationViewAdapter
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class LocationViewDetailFragment : Fragment() {

    private var locationId: Int? = null
    private val viewModel: LocationViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter

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
        val name: TextView = view.findViewById(R.id.editTextNameViewDetail)
        val startDate: TextView = view.findViewById(R.id.editTextStartDateViewDetail)
        val endDate: TextView = view.findViewById(R.id.editTextEndDateViewDetail)
        val checkBoxIsContinues = view.findViewById<CheckBox>(R.id.checkBoxIsContinuesViewDetail)
        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesLocationRecyclerViewDetail)

        viewModel.getLocationById(locationId!!).observe(viewLifecycleOwner, Observer { location ->
            name.text = location?.name
            startDate.text = location?.startDate?.let { dateFormat.format(it) } ?: "N/A"
            endDate.text = location?.endDate?.let { dateFormat.format(it) } ?: "N/A"
            checkBoxIsContinues.isChecked = location?.isContinues!!

            imageAdapter = ImagesAdapter(mutableListOf())
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            location.imagePaths?.let { imageUrls ->
                imageAdapter.updateImages(imageUrls)
            }
        })

        val deleteButton: Button = view.findViewById(R.id.button_delete_location)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this location?")
            .setPositiveButton("Delete") { dialog, which ->
                // Code to delete the item goes here
            }
            .setNegativeButton("Cancel", null)
            .show()
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