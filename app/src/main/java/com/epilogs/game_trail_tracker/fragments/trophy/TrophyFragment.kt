package com.epilogs.game_trail_tracker.fragments.trophy

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.TrophyViewAdapter
import com.epilogs.game_trail_tracker.data.TrophyFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Trophy
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyBinding
import com.epilogs.game_trail_tracker.interfaces.FilterTrophyCriteriaListener
import com.epilogs.game_trail_tracker.interfaces.OnTrophyItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel

class TrophyFragment : Fragment(R.layout.fragment_trophy), OnTrophyItemClickListener,
    FilterTrophyCriteriaListener {

    private val viewModel: AnimalViewModel by activityViewModels()
    private lateinit var binding: FragmentTrophyBinding
    private lateinit var adapter: TrophyViewAdapter
    private var currentSearchText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyBinding.bind(view)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        setupActionBarDrawerToggle()
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

            binding.searchTrophy.setIconified(false);
            // Ensure the view is visible before requesting focus
            binding.searchTrophy.post {
                binding.searchTrophy.requestFocus()
                binding.searchTrophy.requestFocusFromTouch()

                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.searchTrophy, InputMethodManager.SHOW_IMPLICIT)
            }
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

    override fun onTrophyItemClick(trophy: Trophy) {
        val action = TrophyFragmentDirections.actionTrophyFragmentToTrophyViewDetailFragment(trophy.id!!)
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
            navigateToAddTrophy()
        }

        binding.addTrophyButtonFloat.setOnClickListener {
            navigateToAddTrophy()
        }
    }

    private fun navigateToAddTrophy() {
        val action = TrophyFragmentDirections.actionTrophyFragmentToTrophyAddBasicDetailsFragment("trophyFragment")
        findNavController().navigate(action)
    }

    private fun setupActionBarDrawerToggle() {
        val drawerLayout: DrawerLayout = binding.drawerLayoutWeapon
        val toggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
}