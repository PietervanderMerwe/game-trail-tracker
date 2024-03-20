package com.epilogs.game_trail_tracker.fragments.view.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epilogs.game_trail_tracker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdvancedLocationFilterFragment : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate your custom layout for the fragment
        return inflater.inflate(R.layout.fragment_advanced_location_filter, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AdvancedLocationFilterFragment().apply {

            }
    }
}