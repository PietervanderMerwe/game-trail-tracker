package com.epilogs.game_trail_tracker.fragments.weapon

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
import com.epilogs.game_trail_tracker.adapters.WeaponViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponBinding
import com.epilogs.game_trail_tracker.interfaces.OnWeaponItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

class WeaponFragment : Fragment(R.layout.fragment_weapon), OnWeaponItemClickListener {

    private val viewModel: WeaponViewModel by viewModels()
    private lateinit var binding: FragmentWeaponBinding
    private lateinit var adapter: WeaponViewAdapter
    private var currentSearchText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeaponBinding.bind(view)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        with(binding.weaponViewList) {
            layoutManager = LinearLayoutManager(context)
            adapter = WeaponViewAdapter(emptyList(), this@WeaponFragment).also {
                this@WeaponFragment.adapter = it
            }
        }
    }

    private fun setupSearchView() {
        binding.searchIcon.setOnClickListener {
            binding.searchWeapon.visibility = View.VISIBLE
            binding.searchIcon.visibility = View.GONE

            binding.searchWeapon.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchWeapon, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.searchWeapon.setOnCloseListener {
            binding.searchWeapon.visibility = View.GONE
            binding.searchIcon.visibility = View.VISIBLE
            true
        }

        binding.searchWeapon.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchText = newText
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.getAllWeapons().observe(viewLifecycleOwner) { weapons ->
            adapter.updateLocations(weapons)
            checkDataAndUpdateUI()
        }
    }

    override fun onWeaponItemClick(weapon: Weapon) {
        val action = WeaponFragmentDirections.actionWeaponFragmentToWeaponViewDetailFragment(weapon.id!!)
        findNavController().navigate(action)
    }

    private fun checkDataAndUpdateUI() {
        val hasData = adapter.itemCount > 0
        binding.weaponLayoutList.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addWeaponButtonFloat.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addWeaponButton.visibility = if (hasData) View.GONE else View.VISIBLE

        binding.addWeaponButton.setOnClickListener {
            val action =
                WeaponFragmentDirections.actionWeaponFragmentToWeaponAddFragment()
            findNavController().navigate(action)
        }

        binding.addWeaponButtonFloat.setOnClickListener {
            val action =
                WeaponFragmentDirections.actionWeaponFragmentToWeaponAddFragment()
            findNavController().navigate(action)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = WeaponFragment().apply {
            }
    }
}