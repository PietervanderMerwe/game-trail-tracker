package com.epilogs.game_trail_tracker.fragments.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel

class LocationViewDetailFragment : Fragment() {

    private var locationId: Int? = null
    private val viewModel: LocationViewModel by viewModels()

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
        val name: TextView = view.findViewById(R.id.editTextNameViewDetail)
        val startDate: TextView = view.findViewById(R.id.editTextStartDateViewDetail)
        val endDate: TextView = view.findViewById(R.id.editTextEndDateViewDetail)
        val checkBoxIsContinues = view.findViewById<CheckBox>(R.id.checkBoxIsContinuesViewDetail)
        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesLocationRecyclerViewDetail)
        val location = viewModel.getLocationById(locationId!!)
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