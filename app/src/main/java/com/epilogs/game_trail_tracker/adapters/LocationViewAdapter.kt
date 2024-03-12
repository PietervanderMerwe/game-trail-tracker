package com.epilogs.game_trail_tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Location
import java.text.SimpleDateFormat
import java.util.Locale

class LocationViewAdapter(private var locations: List<Location>) : RecyclerView.Adapter<LocationViewAdapter.LocationViewHolder>() {

    class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(location: Location) {
            // Bind your data to your views here
            itemView.findViewById<TextView>(R.id.location_view_item_name).text = location.name
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val startDateStr = location.startDate?.let { dateFormat.format(it) } ?: "N/A"
            val endDateStr = location.endDate?.let { dateFormat.format(it) } ?: "N/A"

            itemView.findViewById<TextView>(R.id.location_view_item_dates).text = "$startDateStr - $endDateStr"
            // Load the first image from imagePaths as an example
            location.imagePaths?.let {
                if (it.isNotEmpty()) {
                    // Assuming you're using Glide or a similar library to load images
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.location_view_item_image))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount() = locations.size

    fun updateLocations(newLocations: List<Location>) {
        locations = newLocations
        notifyDataSetChanged() // Notify the adapter to refresh the data
    }
}