package com.epilogs.game_trail_tracker.fragments.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
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
        val editTextSpecieName: TextView = view.findViewById(R.id.editTextSpecieNameViewDetail)
        val editTextDate: TextView = view.findViewById(R.id.editTextDateViewDetail)
        val editTextWeight: TextView = view.findViewById(R.id.editTextWeightViewDetail)
        val editTextMeasurement: TextView = view.findViewById(R.id.editTextMeasurementViewDetail)
        val locationViewDetail: TextView = view.findViewById(R.id.LocationViewDetail)
        val weaponViewDetail: TextView = view.findViewById(R.id.WeaponViewDetail)
        val imagesRecyclerView =
            view.findViewById<RecyclerView>(R.id.imagesAnimalRecyclerViewViewDetail)

        animalViewModel.getAnimalById(animalId!!).observe(viewLifecycleOwner, Observer { animal ->
            editTextSpecieName.text = animal?.name
            editTextDate.text = animal?.harvestDate?.let { dateFormat.format(it) } ?: "N/A"
            editTextWeight.text = animal?.weight?.toString()
            editTextMeasurement.text = animal?.measurement?.toString()

            animal?.weaponId?.let { weaponId ->
                weaponViewModel.getWeaponById(weaponId)
                    .observe(viewLifecycleOwner, Observer { weapon ->
                        weaponViewDetail.text = weapon?.name
                    })
            }

            animal?.locationId?.let { animalId ->
                locationViewModel.getLocationById(animalId)
                    .observe(viewLifecycleOwner, Observer { location ->
                        locationViewDetail.text = location?.name
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

        val deleteButton: Button = view.findViewById(R.id.button_delete_animal)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this animal?")
            .setPositiveButton("Delete") { dialog, which ->
                // Code to delete the item goes here
            }
            .setNegativeButton("Cancel", null)
            .show()
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