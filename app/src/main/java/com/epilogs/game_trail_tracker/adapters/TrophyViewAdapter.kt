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
import com.epilogs.game_trail_tracker.data.TrophyFilterCriteria
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.interfaces.OnTrophyItemClickListener
import java.text.SimpleDateFormat
import java.util.Locale

class TrophyViewAdapter(private var animals: List<Animal>,
                        private val listener: OnTrophyItemClickListener
) : RecyclerView.Adapter<TrophyViewAdapter.AnimalViewHolder>(),
    Filterable {

    private var animalsFiltered = animals
    var currentFilterCriteria: TrophyFilterCriteria? = null
    private var currentSearchText: String? = ""

    class AnimalViewHolder(view: View, private val listener: OnTrophyItemClickListener) : RecyclerView.ViewHolder(view) {
        fun bind(animal: Animal) {
            itemView.findViewById<TextView>(R.id.animal_view_item_name).text = animal.name
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val harvestDateStr = animal.harvestDate?.let { dateFormat.format(it) } ?: "N/A"

            itemView.findViewById<TextView>(R.id.animal_view_item_date).text = "$harvestDateStr"
            animal.imagePaths?.let {
                if (it.isNotEmpty()) {
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.animal_view_item_image))
                }
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onAnimalItemClick(animal)
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
                    animals
                } else {
                    animals.filter { animal ->
                        val matchesSearch = charSearch.isEmpty() || animal.name.contains(charSearch, ignoreCase = true)
                        val matchesCriteria = currentFilterCriteria?.let { criteria ->
                            val matchesStartDate = criteria.startDate?.let { animal.harvestDate?.compareTo(it) ?: -1 } ?: -1 >= 0
                            val matchesEndDate = criteria.endDate?.let { animal.harvestDate?.compareTo(it) ?: 1 } ?: 1 <= 0
                            matchesStartDate && matchesEndDate
                        } ?: true

                        matchesSearch && matchesCriteria
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                animalsFiltered = results?.values as? List<Animal> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.animal_view_item, parent, false)
        return AnimalViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.bind(animalsFiltered[position])
    }

    override fun getItemCount() = animalsFiltered.size

    fun updateAnimals(newAnimals: List<Animal>) {
        animals = newAnimals
        animalsFiltered = newAnimals
        applyFilter()
    }

    fun updateFilterCriteria(criteria: TrophyFilterCriteria?) {
        currentFilterCriteria = criteria
        applyFilter()
    }

    private fun applyFilter() {
        filter.filter(currentSearchText)
    }
}