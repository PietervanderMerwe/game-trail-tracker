package com.epilogs.game_trail_tracker.fragments.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.LocationViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.interfaces.OnLocationItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.LocationViewModel

class LocationViewFragment : Fragment(), OnLocationItemClickListener {

    private val viewModel: LocationViewModel by viewModels()
    private var locationItemClickListener: OnLocationItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            locationItemClickListener = parentFragment as? OnLocationItemClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$parentFragment must implement OnLocationItemClickListener")
        }
    }

    // Ensure you nullify the reference on detach to prevent memory leaks
    override fun onDetach() {
        super.onDetach()
        locationItemClickListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.location_view_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = LocationViewAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.search_location_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        viewModel.getAllLocations().observe(viewLifecycleOwner, Observer { locations ->
            adapter.updateLocations(locations)
        })

    }

    override fun onLocationItemClick(location: Location) {
        val action = ViewFragmentDirections.actionViewFragmentToLocationViewDetailFragment(location.id!!)
        findNavController().navigate(action)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationViewFragment().apply {
            }
    }
}