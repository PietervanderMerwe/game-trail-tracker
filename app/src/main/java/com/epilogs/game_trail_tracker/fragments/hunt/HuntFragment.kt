package com.epilogs.game_trail_tracker.fragments.hunt

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.HuntViewAdapter
import com.epilogs.game_trail_tracker.data.HuntFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.databinding.FragmentHuntBinding
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
        setupActionBarDrawerToggle()
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
        binding.searchIcon.setOnClickListener {
            binding.searchHunt.visibility = View.VISIBLE
            binding.searchIcon.visibility = View.GONE

            binding.searchHunt.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchHunt, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.searchHunt.setOnCloseListener {
            binding.searchHunt.visibility = View.GONE
            binding.searchIcon.visibility = View.VISIBLE
            true
        }

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
        viewModel.getAllHunts().observe(viewLifecycleOwner) { hunts ->
            adapter.updateLocations(hunts)
            checkDataAndUpdateUI()
        }
    }

    override fun onHuntItemClick(location: Hunt) {
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
        binding.searchIcon.visibility = if (hasData) View.VISIBLE else View.GONE
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

    private fun setupActionBarDrawerToggle() {
        val drawerLayout: DrawerLayout = binding.drawerLayoutWeapon
        val toggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HuntFragment().apply {
        }
    }
}