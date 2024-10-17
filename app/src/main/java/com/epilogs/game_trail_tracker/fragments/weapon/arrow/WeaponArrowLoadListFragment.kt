package com.epilogs.game_trail_tracker.fragments.weapon.arrow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ArrowViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Arrow
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponArrowLoadListBinding
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponViewDetailFragmentDirections
import com.epilogs.game_trail_tracker.interfaces.OnArrowItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.ArrowViewModel

class WeaponArrowLoadListFragment : Fragment(R.layout.fragment_weapon_arrow_load_list),
    OnArrowItemClickListener {

    private var weaponId: Int? = null
    private val viewModel: ArrowViewModel by viewModels()
    private lateinit var binding: FragmentWeaponArrowLoadListBinding
    private lateinit var adapter: ArrowViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weaponId = arguments?.getInt("weaponId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weapon_arrow_load_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeaponArrowLoadListBinding.bind(view)

        setupRecyclerView()
        getArrows()
        setButton()
        setupInsertAndUpdateCheck()
        checkDataAndUpdateUI()
    }

    override fun onArrowItemClick(arrow: Arrow) {
        val action = WeaponViewDetailFragmentDirections.actionWeaponViewDetailFragmentToArrowViewDetailFragment(arrow.id!!)
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        with(binding.weaponArrowViewList) {
            layoutManager = LinearLayoutManager(context)
            adapter = ArrowViewAdapter(emptyList(), this@WeaponArrowLoadListFragment).also {
                this@WeaponArrowLoadListFragment.adapter = it
            }
        }
    }

    private fun getArrows() {
        viewModel.getArrowByWeaponId(weaponId!!).observe(viewLifecycleOwner) { arrows ->
            adapter.updateArrows(arrows)
            checkDataAndUpdateUI()
        }
    }

    private fun setButton() {
        binding.addArrowButton.setOnClickListener {
            navigateToAdd()
        }
        binding.addArrowButtonFloat.setOnClickListener {
            navigateToAdd()
        }
    }

    private fun navigateToAdd()  {
        val action =
            WeaponViewDetailFragmentDirections.actionWeaponViewDetailFragmentToArrowAddFragment(
                weaponId!!
            )
        findNavController().navigate(action)
    }

    private fun setupInsertAndUpdateCheck() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner) {
            getArrows()
        }
        viewModel.getUpdateSuccess().observe(viewLifecycleOwner) {
            getArrows()
        }
    }

    private fun checkDataAndUpdateUI() {
        val hasData = adapter.itemCount > 0

        binding.addArrowButtonFloat.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addArrowButton.visibility = if (hasData) View.GONE else View.VISIBLE
    }

    companion object {
        fun newInstance(weaponId: Int?): WeaponArrowLoadListFragment {
            val fragment = WeaponArrowLoadListFragment()
            val args = Bundle()
            args.putInt("weaponId", weaponId!!)
            fragment.arguments = args
            return fragment
        }
    }
}