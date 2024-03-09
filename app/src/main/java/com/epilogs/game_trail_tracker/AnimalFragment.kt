package com.epilogs.game_trail_tracker

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.adapters.LocationAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnimalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnimalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel: AnimalViewModel by viewModels()
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
        return inflater.inflate(R.layout.fragment_animal, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextSpecieName: EditText = view.findViewById(R.id.editTextSpecieName)
        val editTextDate : EditText = view.findViewById(R.id.editTextDate)
        val editTextWeight : EditText = view.findViewById(R.id.editTextWeight)
        val editTextMeasurement : EditText = view.findViewById(R.id.editTextMeasurement)
        val spinnerLocation: Spinner = view.findViewById(R.id.spinnerLocation)
        val spinnerWeapon: Spinner = view.findViewById(R.id.spinnerWeapon)
        val buttonSelectAnimalImages: Button = view.findViewById(R.id.buttonSelectAnimalImages)
        val buttonSaveAnimal: Button = view.findViewById(R.id.buttonSaveAnimal)
        val dateConverter = DateConverter()

        editTextDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                editTextDate.setText(selectedDate)
            }
        }

        val locations = mutableListOf<Location>()
        val locationAdapter = LocationAdapter(requireContext(), locations)
        spinnerLocation.adapter = locationAdapter

        viewModel.getAllLocations().observe(viewLifecycleOwner) { newLocations  ->
            locationAdapter.clear()
            locationAdapter.addAll(newLocations )
            locationAdapter.notifyDataSetChanged()
        }

        val weapons = mutableListOf<Weapon>()
        val weaponAdapter = WeaponAdapter(requireContext(), weapons)
        spinnerWeapon.adapter = weaponAdapter

        viewModel.getAllWeapons().observe(viewLifecycleOwner) { newWeapons  ->
            weaponAdapter.clear()
            weaponAdapter.addAll(newWeapons)
            weaponAdapter.notifyDataSetChanged()
        }

        val imagesRecyclerView: RecyclerView = view.findViewById(R.id.imagesRecyclerView)
        imagesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Example image URIs, replace with your actual image URIs
        val imageUris = listOf(
            Uri.parse("android.resource://your.package.name/drawable/image1"),
            Uri.parse("android.resource://your.package.name/drawable/image2")
        )

        imagesRecyclerView.adapter = ImagesAdapter(imageUris)

        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedLocation = parent.getItemAtPosition(position) as Location
                val locationId = selectedLocation.id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected if needed
            }
        }

        buttonSaveAnimal.setOnClickListener {
            val name = editTextSpecieName.text.toString()
            val weightString  = editTextWeight.text.toString()
            val measurementString  = editTextMeasurement.text.toString()
            val dateString = editTextDate.text.toString()

            val date = dateConverter.parseDate(dateString)
            val notes = "Some notes"
            val imagePaths = listOf<String>()
            val weight = weightString.toDoubleOrNull() ?: 0.0
            val measurement = measurementString.toDoubleOrNull() ?: 0.0
            val animal = Animal(name = name, weight = weight, measurement = measurement, harvestDate = date, notes = notes, locationId = 1, weaponId = 1,imagePaths = imagePaths)

            viewModel.insertAnimal(animal)
        }

        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                editTextSpecieName.text.clear()
                editTextDate.text.clear()
                editTextWeight.text.clear()
                editTextMeasurement.text.clear()
                viewModel.resetInsertionSuccess()

                val checkMarkImageView: ImageView = view.findViewById(R.id.checkMarkAnimalAdd)
                checkMarkImageView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    checkMarkImageView.visibility = View.GONE
                }, 3000)
            }
        })
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnimalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}