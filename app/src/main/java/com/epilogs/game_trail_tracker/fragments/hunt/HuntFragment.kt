package com.epilogs.game_trail_tracker.fragments.hunt

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.HuntViewAdapter
import com.epilogs.game_trail_tracker.data.HuntFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.databinding.FragmentHuntBinding
import com.epilogs.game_trail_tracker.fragments.view.filter.AdvancedHuntFilterFragment
import com.epilogs.game_trail_tracker.interfaces.FilterHuntCriteriaListener
import com.epilogs.game_trail_tracker.interfaces.OnHuntItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel

class HuntFragment : Fragment(R.layout.fragment_hunt), OnHuntItemClickListener,
    FilterHuntCriteriaListener {

    private val viewModel: HuntViewModel by viewModels()
    private lateinit var binding: FragmentHuntBinding
    private lateinit var adapter: HuntViewAdapter
    private var currentSearchText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHuntBinding.bind(view)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        setupAdvancedFilterButton()
    }

    private fun setupRecyclerView() {
        with(binding.huntViewList) {
            layoutManager = LinearLayoutManager(context)
            adapter = HuntViewAdapter(emptyList(), this@HuntFragment).also {
                this@HuntFragment.adapter = it
            }
        }
    }

    private fun setupSearchView() {
        binding.searchHunt.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchText = newText
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.getAllLocations().observe(viewLifecycleOwner) { hunts ->
            adapter.updateLocations(hunts)
            checkDataAndUpdateUI()
        }
    }

    private fun setupAdvancedFilterButton() {
        binding.advancedHuntFilterButton.setOnClickListener {
            AdvancedHuntFilterFragment.newInstance(adapter.currentFilterCriteria)
                .show(childFragmentManager, null)
        }
    }

    override fun onHuntItemClick(location: Location) {
        val action = HuntFragmentDirections.actionHuntFragmentToHuntViewDetailFragment(location.id!!)
       findNavController().navigate(action)
    }

    override fun onFilterCriteriaSelected(criteria: HuntFilterCriteria?) {
        adapter.updateFilterCriteria(criteria)
    }

    private fun checkDataAndUpdateUI() {
        val hasData = adapter.itemCount > 0
        binding.huntLayoutList.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addHuntButtonFloat.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addHuntButton.visibility = if (hasData) View.GONE else View.VISIBLE

        binding.addHuntButton.setOnClickListener {
            val action =
                HuntFragmentDirections.actionHuntFragmentToHuntAddFragment()
            findNavController().navigate(action)
        }

        binding.addHuntButtonFloat.setOnClickListener {
            val action =
                HuntFragmentDirections.actionHuntFragmentToHuntAddFragment()
            findNavController().navigate(action)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = HuntFragment().apply {
        }
    }
}