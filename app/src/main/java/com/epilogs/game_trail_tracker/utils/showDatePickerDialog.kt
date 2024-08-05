package com.epilogs.game_trail_tracker.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.epilogs.game_trail_tracker.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun showDatePickerDialog(
    context: Context,
    setDate: (String) -> Unit,
    minDate: Calendar? = null,
    maxDate: Calendar? = null
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val constraintsBuilder = CalendarConstraints.Builder()

    minDate?.let {
        constraintsBuilder.setStart(it.timeInMillis)
    }
    maxDate?.let {
        constraintsBuilder.setEnd(it.timeInMillis)
    }

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select Date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .setCalendarConstraints(constraintsBuilder.build())
        .setTheme(R.style.CustomDatePickerTheme)
        .build()

    datePicker.show((context as AppCompatActivity).supportFragmentManager, datePicker.toString())

    datePicker.addOnPositiveButtonClickListener { selection ->
        selection?.let {
            val selectedDate = formatter.format(Date(it))
            setDate(selectedDate)
        }
    }
}