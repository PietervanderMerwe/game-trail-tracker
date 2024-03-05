package com.epilogs.game_trail_tracker.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar

fun showDatePickerDialog(context: Context, setDate: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        // Format the selected date and return it via the callback
        val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
        setDate(selectedDate)
    }, year, month, dayOfMonth).show()
}