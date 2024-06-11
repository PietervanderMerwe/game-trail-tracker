package com.epilogs.game_trail_tracker.utils

import android.app.DatePickerDialog
import android.content.Context
import android.view.ContextThemeWrapper
import com.epilogs.game_trail_tracker.R
import java.util.Calendar

fun showDatePickerDialog(
    context: Context,
    setDate: (String) -> Unit,
    minDate: Calendar? = null,
    maxDate: Calendar? = null
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        ContextThemeWrapper(context, R.style.CustomDatePickerDialogTheme),
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
            setDate(selectedDate)
        },
        year, month, dayOfMonth
    )

    minDate?.let {
        datePickerDialog.datePicker.minDate = it.timeInMillis
    }

    maxDate?.let {
        datePickerDialog.datePicker.maxDate = it.timeInMillis
    }

    datePickerDialog.show()
}