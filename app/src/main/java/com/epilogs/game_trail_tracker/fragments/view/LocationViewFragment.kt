package com.epilogs.game_trail_tracker.fragments.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.LocationViewAdapter
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel

class LocationViewFragment : Fragment() {

    private val viewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.location_view_list).apply {
            layoutManager = LinearLayoutManager(context)
        }

        val adapter = LocationViewAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.getAllLocations().observe(viewLifecycleOwner, Observer { locations ->
            adapter.updateLocations(locations)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationViewFragment().apply {
            }
    }
}