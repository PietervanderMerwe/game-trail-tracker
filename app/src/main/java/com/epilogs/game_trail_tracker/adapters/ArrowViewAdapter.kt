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
import com.epilogs.game_trail_tracker.database.entities.Arrow
import com.epilogs.game_trail_tracker.interfaces.OnArrowItemClickListener

class ArrowViewAdapter (
    private var arrows: List<Arrow>,
    private val listener: OnArrowItemClickListener
) : RecyclerView.Adapter<ArrowViewAdapter.ArrowViewHolder>(),
    Filterable {

    private var arrowsFiltered = arrows
    private var currentSearchText: String? = ""

    class ArrowViewHolder(view: View, private val listener: OnArrowItemClickListener) :
        RecyclerView.ViewHolder(view) {
        fun bind(arrow: Arrow) {
            itemView.findViewById<TextView>(R.id.arrow_view_item_name).text = arrow.manufacturer
            itemView.findViewById<TextView>(R.id.arrow_view_item_type).text = arrow.type

            arrow.imagePaths?.let {
                if (it.isNotEmpty()) {
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.arrow_view_item_image))
                }
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onArrowItemClick(arrow)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                arrowsFiltered = if (charSearch.isEmpty()) {
                    arrows
                } else {
                    arrows.filter {
                        it.manufacturer?.contains(charSearch, ignoreCase = true)!!
                    }
                }
                return FilterResults().apply { values = arrowsFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                arrowsFiltered = results?.values as? List<Arrow> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArrowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.arrow_view_item, parent, false)
        return ArrowViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ArrowViewHolder, position: Int) {
        holder.bind(arrowsFiltered[position])
    }

    override fun getItemCount() = arrowsFiltered.size

    fun updateArrows(newArrows: List<Arrow>) {
        arrows = newArrows
        arrowsFiltered = newArrows
        applyFilter()
    }

    private fun applyFilter() {
        filter.filter(currentSearchText)
    }
}