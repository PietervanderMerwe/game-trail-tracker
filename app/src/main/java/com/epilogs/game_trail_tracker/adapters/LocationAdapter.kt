package com.epilogs.game_trail_tracker.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.epilogs.game_trail_tracker.database.entities.Location

class LocationAdapter(context: Context, locations: List<Location>) :
    ArrayAdapter<Location>(context, android.R.layout.simple_spinner_dropdown_item, locations) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent).apply {
            (this as TextView).text = getItem(position)?.name
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent).apply {
            (this as TextView).text = getItem(position)?.name
        }
    }
}