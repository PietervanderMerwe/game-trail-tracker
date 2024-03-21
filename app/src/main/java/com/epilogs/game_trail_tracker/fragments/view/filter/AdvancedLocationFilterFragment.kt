package com.epilogs.game_trail_tracker.fragments.view.filter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.LocationViewAdapter
import com.epilogs.game_trail_tracker.data.LocationFilterCriteria
import com.epilogs.game_trail_tracker.interfaces.FilterCriteriaListener
import com.epilogs.game_trail_tracker.interfaces.OnLocationItemClickListener
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdvancedLocationFilterFragment : BottomSheetDialogFragment() {

    private var listener: FilterCriteriaListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            parentFragment is FilterCriteriaListener -> parentFragment as FilterCriteriaListener
            context is FilterCriteriaListener -> context as FilterCriteriaListener
            else -> throw RuntimeException("$context must implement FilterCriteriaListener")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_advanced_location_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startDate: EditText = view.findViewById(R.id.editTextStartDateLocationFilter)
        val endDate: EditText = view.findViewById(R.id.editTextEndDateLocationFilter)
        val applyButton: Button = view.findViewById(R.id.apply_filters_button)

        startDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                startDate.setText(selectedDate)
            }
        }

        endDate.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                endDate.setText(selectedDate)
            }
        }

        applyButton.setOnClickListener {
            val dateConverter = DateConverter()
            val criteria = LocationFilterCriteria(
                startDate = dateConverter.parseDate(startDate.text.toString()),
                endDate = dateConverter.parseDate(endDate.text.toString())
            )

            listener?.onFilterCriteriaSelected(criteria)
            dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AdvancedLocationFilterFragment().apply {

            }
    }
}