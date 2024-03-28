package com.epilogs.game_trail_tracker.fragments.hunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.HuntViewAdapter
import com.epilogs.game_trail_tracker.data.LocationFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.fragments.trophy.TrophyViewFragment
import com.epilogs.game_trail_tracker.fragments.view.filter.AdvancedLocationFilterFragment
import com.epilogs.game_trail_tracker.interfaces.FilterLocationCriteriaListener
import com.epilogs.game_trail_tracker.interfaces.OnLocationItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel

class HuntFragment : Fragment(), OnLocationItemClickListener, FilterLocationCriteriaListener {

    private val viewModel: HuntViewModel by viewModels()
    private lateinit var adapter: HuntViewAdapter
    private var currentSearchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.hunt_view_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = HuntViewAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.search_hunt)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchText = newText
                adapter.filter.filter(newText)
                return true
            }
        })

        viewModel.getAllLocations().observe(viewLifecycleOwner, Observer { hunts ->
            adapter.updateLocations(hunts)
        })

        val advancedFilterButton: ImageView = view.findViewById(R.id.advanced_hunt_filter_button)
        advancedFilterButton.setOnClickListener {
            val advancedFilterFragment = AdvancedLocationFilterFragment.newInstance(adapter.currentFilterCriteria)
            advancedFilterFragment.show(childFragmentManager, advancedFilterFragment.tag)
        }
    }

    override fun onLocationItemClick(location: Location) {
//        val action = ViewFragmentDirections.actionViewFragmentToAnimalViewDetailFragment(animal.id!!)
//        findNavController().navigate(action)
    }

    override fun onFilterCriteriaSelected(criteria: LocationFilterCriteria?) {
        adapter.updateFilterCriteria(criteria)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HuntFragment().apply {
            }
    }
}