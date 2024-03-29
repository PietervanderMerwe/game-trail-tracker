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
import com.epilogs.game_trail_tracker.data.HuntFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.interfaces.OnHuntItemClickListener
import java.text.SimpleDateFormat
import java.util.Locale

class HuntViewAdapter(private var locations: List<Hunt>,
                      private val listener: OnHuntItemClickListener,
) : RecyclerView.Adapter<HuntViewAdapter.LocationViewHolder>(),
    Filterable {

    private var locationsFiltered = locations
    var currentFilterCriteria: HuntFilterCriteria? = null
    private var currentSearchText: String? = ""

    class LocationViewHolder(private val view: View, private val listener: OnHuntItemClickListener) : RecyclerView.ViewHolder(view) {

        fun bind(location: Hunt) {
            itemView.findViewById<TextView>(R.id.location_view_item_name).text = location.name
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDateStr = location.startDate?.let { dateFormat.format(it) } ?: "N/A"
            val endDateStr = location.endDate?.let { dateFormat.format(it) } ?: "N/A"

            itemView.findViewById<TextView>(R.id.location_view_item_dates).text = "$startDateStr - $endDateStr"
            location.imagePaths?.let {
                if (it.isNotEmpty()) {
                    Glide.with(view.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.location_view_item_image))
                }
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onHuntItemClick(location)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""

                val filterResults = FilterResults()
                filterResults.values = if (charSearch.isEmpty() && currentFilterCriteria == null) {
                    locations
                } else {
                    locations.filter { location ->
                        val matchesSearch = charSearch.isEmpty() || location.name.contains(charSearch, ignoreCase = true)
                        val matchesCriteria = currentFilterCriteria?.let { criteria ->
                            val matchesStartDate = criteria.startDate?.let { location.startDate?.compareTo(it) ?: -1 } ?: -1 >= 0
                            val matchesEndDate = criteria.endDate?.let { location.endDate?.compareTo(it) ?: 1 } ?: 1 <= 0
                            matchesStartDate && matchesEndDate
                        } ?: true

                        matchesSearch && matchesCriteria
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                locationsFiltered = results?.values as? List<Hunt> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return LocationViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val locationItem = locationsFiltered[position]
        holder.bind(locationItem)
    }

    override fun getItemCount() = locationsFiltered.size

    fun updateLocations(newLocations: List<Hunt>) {
        locations = newLocations
        locationsFiltered = newLocations
        applyFilter()
    }

    fun updateFilterCriteria(criteria: HuntFilterCriteria?) {
        currentFilterCriteria = criteria
        applyFilter()
    }

    private fun applyFilter() {
        filter.filter(currentSearchText)
    }
}