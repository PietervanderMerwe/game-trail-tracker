package com.epilogs.game_trail_tracker.fragments.view

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
import com.epilogs.game_trail_tracker.adapters.WeaponViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.interfaces.OnWeaponItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

class WeaponViewFragment : Fragment(), OnWeaponItemClickListener {

    private val viewModel: WeaponViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weapon_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.weapon_view_list).apply {
            layoutManager = LinearLayoutManager(context)
        }

        val adapter = WeaponViewAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.search_weapon_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // if you want to handle the submit button click
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        viewModel.getAllWeapons().observe(viewLifecycleOwner, Observer { weapons ->
            adapter.updateLocations(weapons)
        })
    }

    override fun onWeaponItemClick(weapon: Weapon) {
        val action = ViewFragmentDirections.actionViewFragmentToWeaponViewDetailFragment(weapon.id!!)
        findNavController().navigate(action)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeaponViewFragment().apply {
            }
    }
}