package com.epilogs.game_trail_tracker.fragments.trophy

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.TrophyViewAdapter
import com.epilogs.game_trail_tracker.data.TrophyFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyBinding
import com.epilogs.game_trail_tracker.fragments.view.filter.AdvancedTrophyFilterFragment
import com.epilogs.game_trail_tracker.interfaces.FilterTrophyCriteriaListener
import com.epilogs.game_trail_tracker.interfaces.OnTrophyItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel

class TrophyFragment : Fragment(R.layout.fragment_trophy), OnTrophyItemClickListener,
    FilterTrophyCriteriaListener {

    private val viewModel: AnimalViewModel by viewModels()
    private lateinit var binding: FragmentTrophyBinding
    private lateinit var adapter: TrophyViewAdapter
    private var currentSearchText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyBinding.bind(view)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        with(binding.trophyViewList) {
            layoutManager = LinearLayoutManager(context)
            adapter = TrophyViewAdapter(emptyList(), this@TrophyFragment).also {
                this@TrophyFragment.adapter = it
            }
        }
    }

    private fun setupSearchView() {
        binding.searchIcon.setOnClickListener {
            binding.searchTrophy.visibility = View.VISIBLE
            binding.searchIcon.visibility = View.GONE

            binding.searchTrophy.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchTrophy, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.searchTrophy.setOnCloseListener {
            binding.searchTrophy.visibility = View.GONE
            binding.searchIcon.visibility = View.VISIBLE
            true
        }

        binding.searchTrophy.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchText = newText
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.getAllAnimals().observe(viewLifecycleOwner) { trophies ->
            adapter.updateAnimals(trophies)
            checkDataAndUpdateUI()
        }
    }

    override fun onTrophyItemClick(animal: Animal) {
        val action = TrophyFragmentDirections.actionTrophyFragmentToTrophyViewDetailFragment(animal.id!!)
        findNavController().navigate(action)
    }

    override fun onFilterCriteriaSelected(criteria: TrophyFilterCriteria?) {
        adapter.updateFilterCriteria(criteria)
    }

    private fun checkDataAndUpdateUI() {
        val hasData = adapter.itemCount > 0
        binding.trophyLayoutList.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addTrophyButtonFloat.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.searchIcon.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addTrophyButton.visibility = if (hasData) View.GONE else View.VISIBLE

        binding.addTrophyButton.setOnClickListener {
            val action =
                TrophyFragmentDirections.actionTrophyFragmentToTrophyAddFragment()
            findNavController().navigate(action)
        }

        binding.addTrophyButtonFloat.setOnClickListener {
            val action =
                TrophyFragmentDirections.actionTrophyFragmentToTrophyAddFragment()
            findNavController().navigate(action)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = TrophyFragment().apply {
            }
    }
}