package com.epilogs.game_trail_tracker.fragments.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.AnimalViewAdapter
import com.epilogs.game_trail_tracker.adapters.LocationViewAdapter
import com.epilogs.game_trail_tracker.data.AnimalFilterCriteria
import com.epilogs.game_trail_tracker.data.LocationFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.fragments.view.filter.AdvancedAnimalFilterFragment
import com.epilogs.game_trail_tracker.fragments.view.filter.AdvancedLocationFilterFragment
import com.epilogs.game_trail_tracker.interfaces.FilterAnimalCriteriaListener
import com.epilogs.game_trail_tracker.interfaces.OnAnimalItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel

class AnimalViewFragment : Fragment(), OnAnimalItemClickListener, FilterAnimalCriteriaListener {

    private val viewModel: AnimalViewModel by viewModels()
    private lateinit var adapter: AnimalViewAdapter
    private var currentSearchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_animal_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.animal_view_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AnimalViewAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.search_animal_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // if you want to handle the submit button click
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchText = newText
                adapter.filter.filter(newText)
                return true
            }
        })

        viewModel.getAllAnimals().observe(viewLifecycleOwner, Observer { animals ->
            adapter.updateAnimals(animals)
        })

        val advancedFilterButton: ImageView = view.findViewById(R.id.advanced_animal_filter_button)
        advancedFilterButton.setOnClickListener {
            val advancedFilterFragment = AdvancedAnimalFilterFragment.newInstance(adapter.currentFilterCriteria)
            advancedFilterFragment.show(childFragmentManager, advancedFilterFragment.tag)
        }
    }

    override fun onAnimalItemClick(animal: Animal) {
        val action = ViewFragmentDirections.actionViewFragmentToAnimalViewDetailFragment(animal.id!!)
        findNavController().navigate(action)
    }

    override fun onFilterCriteriaSelected(criteria: AnimalFilterCriteria?) {
        adapter.updateFilterCriteria(criteria)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnimalViewFragment().apply {
            }
    }
}