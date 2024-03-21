package com.epilogs.game_trail_tracker.fragments.view.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epilogs.game_trail_tracker.R

class AdvancedWeaponFilterFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advanced_weapon_filter, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AdvancedWeaponFilterFragment().apply {

            }
    }
}