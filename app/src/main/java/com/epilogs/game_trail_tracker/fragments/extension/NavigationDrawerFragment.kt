package com.epilogs.game_trail_tracker.fragments.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponFragmentDirections

class NavigationDrawerFragment : Fragment() {

    private lateinit var navView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false)
        navView = view.findViewById(R.id.nav_view_drawer)
        setupNavigationDrawer()
        return view
    }

    private fun setupNavigationDrawer() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.huntFragment)
                }

                R.id.nav_settings -> {
                    findNavController().navigate(R.id.userSettingsFragment)
                }
            }
            true
        }
    }
}