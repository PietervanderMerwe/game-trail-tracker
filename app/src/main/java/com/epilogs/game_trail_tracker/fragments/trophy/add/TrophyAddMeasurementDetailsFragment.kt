
package com.epilogs.game_trail_tracker.fragments.trophy.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epilogs.game_trail_tracker.R

class TrophyAddMeasurementDetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_add_measurement_details, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TrophyAddMeasurementDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}