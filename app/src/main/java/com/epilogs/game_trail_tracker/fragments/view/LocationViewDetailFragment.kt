package com.epilogs.game_trail_tracker.fragments.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.epilogs.game_trail_tracker.R

class LocationViewDetailFragment : Fragment() {
    private var locationId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            locationId = it.getInt("locationId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text: TextView = view.findViewById(R.id.view_detail_text)
        text.text = locationId.toString()
    }

    companion object {
        // If you decide you need param1 and param2, you can include them in the newInstance method
        @JvmStatic fun newInstance(locationId: Long, param1: String? = null, param2: String? = null) =
            LocationViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong("locationId", locationId)
                }
            }
    }
}