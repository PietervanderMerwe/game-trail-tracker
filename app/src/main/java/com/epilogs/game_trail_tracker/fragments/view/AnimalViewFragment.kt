package com.epilogs.game_trail_tracker.fragments.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.AnimalViewAdapter
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel

class AnimalViewFragment : Fragment() {

    private val viewModel: AnimalViewModel by viewModels()

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

        val recyclerView = view.findViewById<RecyclerView>(R.id.animal_view_list).apply {
            layoutManager = LinearLayoutManager(context)
        }

        val adapter = AnimalViewAdapter(emptyList())
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.search_animal_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // if you want to handle the submit button click
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        viewModel.getAllAnimals().observe(viewLifecycleOwner, Observer { animals ->
            adapter.updateAnimals(animals)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnimalViewFragment().apply {
            }
    }
}