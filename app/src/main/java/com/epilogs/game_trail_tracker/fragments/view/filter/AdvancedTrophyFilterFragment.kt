package com.epilogs.game_trail_tracker.fragments.view.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.data.TrophyFilterCriteria
import com.epilogs.game_trail_tracker.interfaces.FilterTrophyCriteriaListener
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdvancedTrophyFilterFragment : BottomSheetDialogFragment() {

    private var listener: FilterTrophyCriteriaListener? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            parentFragment is FilterTrophyCriteriaListener -> parentFragment as FilterTrophyCriteriaListener
            context is FilterTrophyCriteriaListener -> context as FilterTrophyCriteriaListener
            else -> throw RuntimeException("$context must implement FilterAnimalCriteriaListener")
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog as? BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = bottomSheet?.layoutParams
        layoutParams?.height = (resources.displayMetrics.heightPixels * 0.7).toInt()
        bottomSheet?.layoutParams = layoutParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advanced_animal_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startDateEditText: EditText = view.findViewById(R.id.editTextStartDateAnimalFilter)
        val endDateEditText: EditText = view.findViewById(R.id.editTextEndDateAnimalFilter)
        val applyButton: Button = view.findViewById(R.id.apply_animal_filters_button)
        val clearButton: Button = view.findViewById(R.id.clear_animal_filters_button)

        val currentCriteria =
            arguments?.getSerializable(AdvancedTrophyFilterFragment.ARG_FILTER_CRITERIA) as? TrophyFilterCriteria
        currentCriteria?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            startDateEditText.setText(dateFormat.format(it.startDate!!))
            endDateEditText.setText(dateFormat.format(it.endDate!!))
        }

        setupStartDatePicker(startDateEditText)

        setupEndDatePicker(endDateEditText)

        applyButton.setOnClickListener {
            val dateConverter = DateConverter()

            if (startDateEditText.text.isNullOrEmpty() && endDateEditText.text.isNullOrEmpty()) {
                listener?.onFilterCriteriaSelected(null)
            } else {
                val criteria = TrophyFilterCriteria(
                    startDate = dateConverter.parseDate(startDateEditText.text.toString()),
                    endDate = dateConverter.parseDate(endDateEditText.text.toString())
                )
                listener?.onFilterCriteriaSelected(criteria)
            }

            dismiss()
        }

        clearButton.setOnClickListener {
            startDateEditText.setText("")
            endDateEditText.setText("")

            listener?.onFilterCriteriaSelected(null)
        }
    }

    private fun setupStartDatePicker(editText: EditText) {
        editText.setOnClickListener {
            showDatePickerDialog(requireContext(), { selectedDate ->
                editText.setText(selectedDate)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                startDate = Calendar.getInstance().apply {
                    time = formatter.parse(selectedDate) ?: return@apply
                }
            }, maxDate = endDate)
        }
    }

    private fun setupEndDatePicker(editText: EditText) {
        editText.setOnClickListener {
            showDatePickerDialog(requireContext(), { selectedDate ->
                editText.setText(selectedDate)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                endDate = Calendar.getInstance().apply {
                    time = formatter.parse(selectedDate) ?: return@apply
                }
            }, minDate = startDate)
        }
    }

    companion object {
        private const val ARG_FILTER_CRITERIA = "filterCriteria"

        @JvmStatic
        fun newInstance(filterCriteria: TrophyFilterCriteria? = null) =
            AdvancedTrophyFilterFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_FILTER_CRITERIA, filterCriteria)
                }
            }
    }
}