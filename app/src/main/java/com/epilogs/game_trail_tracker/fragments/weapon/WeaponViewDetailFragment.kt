package com.epilogs.game_trail_tracker.fragments.weapon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.WeaponViewPagerAdapter
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponViewDetailBinding
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import com.google.android.material.tabs.TabLayoutMediator

class WeaponViewDetailFragment : Fragment() {
    private var weaponId: Int? = null
    private val viewModel: WeaponViewModel by viewModels()
    private lateinit var binding: FragmentWeaponViewDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weaponId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weapon_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeaponViewDetailBinding.bind(view)

        binding.editIcon.setOnClickListener {
            navigateToEdit()
        }

        getWeapon()
        setAdapter()
    }

    private fun getWeapon() {
        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->
            binding.weaponName.text = weapon?.name
        })
    }

    private fun navigateToEdit() {
        val action = WeaponViewDetailFragmentDirections.actionWeaponViewDetailFragmentToWeaponAddFragment(weaponId!!)
        findNavController().navigate(action)
    }

    private fun setAdapter() {
        weaponId?.let {
            val adapter = WeaponViewPagerAdapter(requireActivity(), weaponId)
            binding.viewPager.adapter = adapter

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Bullets"
                    1 -> "Images"
                    else -> null
                }
            }.attach()
        }
    }

    companion object {
        fun newInstance(weaponId: Int): WeaponViewDetailFragment {
            val fragment = WeaponViewDetailFragment()
            val args = Bundle()
            args.putInt("weaponId", weaponId)
            fragment.arguments = args
            return fragment
        }
    }
}