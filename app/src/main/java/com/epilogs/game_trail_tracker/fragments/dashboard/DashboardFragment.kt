package com.epilogs.game_trail_tracker.fragments.dashboard

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.fragments.view.AnimalViewDetailFragmentDirections
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel
import com.epilogs.game_trail_tracker.views.TrailView
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardFragment : Fragment() {
    private val animalViewModel: AnimalViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var locationFrameLayout: FrameLayout
    private lateinit var animalFrameLayout: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        locationFrameLayout = view.findViewById(R.id.location_frame_layout)
        animalFrameLayout = view.findViewById(R.id.animal_frame_layout)
        animalViewModel.getLatestAnimal().observe(viewLifecycleOwner, Observer { latestAnimal ->
            if (latestAnimal != null) {
                animalFrameLayout.visibility = View.VISIBLE
                val animalName: TextView = view.findViewById(R.id.animal_dashboard_name)
                val animalDate: TextView = view.findViewById(R.id.animal_dashboard_date)
                val animalImageView: ImageView = view.findViewById(R.id.animal_dashboard_image)

                animalName.text = latestAnimal.name
                animalDate.text =
                    latestAnimal.harvestDate?.let { dateFormat.format(latestAnimal.harvestDate!!) }
                        ?: "N/A"
                val imagePaths = latestAnimal.imagePaths
                if (!imagePaths.isNullOrEmpty()) {
                    Glide.with(view.context).load(imagePaths[0]).into(animalImageView)
                }
                animalFrameLayout.setOnClickListener {
                    val action =
                        DashboardFragmentDirections.actionDashboardFragmentToAnimalViewDetailFragment2(
                            latestAnimal.id!!
                        )
                    findNavController().navigate(action)
                }
            } else {
                animalFrameLayout.visibility = View.GONE
            }
        })

        locationViewModel.getLatestLocation()
            .observe(viewLifecycleOwner, Observer { latestLocation ->
                if (latestLocation != null) {
                    locationFrameLayout.visibility = View.VISIBLE
                    val locationName: TextView = view.findViewById(R.id.location_dashboard_name)
                    val locationDate: TextView = view.findViewById(R.id.location_dashboard_dates)
                    val locationImageView: ImageView =
                        view.findViewById(R.id.location_dashboard_image)

                    locationName.text = latestLocation.name
                    val startDateStr =
                        latestLocation.startDate?.let { dateFormat.format(it) } ?: "N/A"
                    val endDateStr = latestLocation.endDate?.let { dateFormat.format(it) } ?: "N/A"

                    locationDate.text = "$startDateStr - $endDateStr"
                    val imagePaths = latestLocation.imagePaths
                    if (!imagePaths.isNullOrEmpty()) {
                        Glide.with(view.context).load(imagePaths[0]).into(locationImageView)
                    }

                    locationFrameLayout.setOnClickListener {
                        val action =
                            DashboardFragmentDirections.actionDashboardFragmentToLocationViewDetailFragment2(
                                latestLocation.id!!
                            )
                        findNavController().navigate(action)
                    }
                } else {
                    locationFrameLayout.visibility = View.GONE
                }
            })

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DashboardFragment().apply {
            }
    }
}