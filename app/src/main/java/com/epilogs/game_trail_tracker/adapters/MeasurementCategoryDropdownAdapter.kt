package com.epilogs.game_trail_tracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.epilogs.game_trail_tracker.database.entities.MeasurementCategory

class MeasurementCategoryDropdownAdapter(context: Context, measurementCategories: List<MeasurementCategory>) :
    ArrayAdapter<MeasurementCategory>(context, android.R.layout.simple_spinner_dropdown_item, measurementCategories) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val measurementCategory = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = measurementCategory?.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}