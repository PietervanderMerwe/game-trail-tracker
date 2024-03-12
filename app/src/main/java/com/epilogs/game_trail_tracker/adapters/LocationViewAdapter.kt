package com.epilogs.game_trail_tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Location
import java.text.SimpleDateFormat
import java.util.Locale

class LocationViewAdapter(private var locations: List<Location>) : RecyclerView.Adapter<LocationViewAdapter.LocationViewHolder>(),
    Filterable {

    private var locationsFiltered = locations

    class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(location: Location) {
            itemView.findViewById<TextView>(R.id.location_view_item_name).text = location.name
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val startDateStr = location.startDate?.let { dateFormat.format(it) } ?: "N/A"
            val endDateStr = location.endDate?.let { dateFormat.format(it) } ?: "N/A"

            itemView.findViewById<TextView>(R.id.location_view_item_dates).text = "$startDateStr - $endDateStr"
            location.imagePaths?.let {
                if (it.isNotEmpty()) {
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.location_view_item_image))
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                locationsFiltered = if (charSearch.isNullOrEmpty()) {
                    locations
                } else {
                    // Apply filtering
                    locations.filter {
                        it.name.contains(charSearch, ignoreCase = true)
                        // Add more conditions here for additional fields like dates
                    }
                }
                return FilterResults().apply { values = locationsFiltered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                locationsFiltered = results?.values as List<Location>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locationsFiltered[position])
    }

    override fun getItemCount() = locationsFiltered.size

    fun updateLocations(newLocations: List<Location>) {
        locations = newLocations
        notifyDataSetChanged() // Notify the adapter to refresh the data
    }
}