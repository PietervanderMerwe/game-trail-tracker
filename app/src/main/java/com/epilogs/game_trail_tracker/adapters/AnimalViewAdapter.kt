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
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.interfaces.OnAnimalItemClickListener
import java.text.SimpleDateFormat
import java.util.Locale

class AnimalViewAdapter(private var animals: List<Animal>,
                        private val listener: OnAnimalItemClickListener
) : RecyclerView.Adapter<AnimalViewAdapter.AnimalViewHolder>(),
    Filterable {

    private var animalsFiltered = animals

    class AnimalViewHolder(view: View, private val listener: OnAnimalItemClickListener) : RecyclerView.ViewHolder(view) {
        fun bind(animal: Animal) {
            itemView.findViewById<TextView>(R.id.animal_view_item_name).text = animal.name
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val harvestDateStr = animal.harvestDate?.let { dateFormat.format(it) } ?: "N/A"

            itemView.findViewById<TextView>(R.id.animal_view_item_date).text = "$harvestDateStr"
            animal.imagePaths?.let {
                if (it.isNotEmpty()) {
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.animal_view_item_image))
                }
            }

            // Move the click listener setup here
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
                val charSearch = constraint.toString()
                animalsFiltered = if (charSearch.isEmpty()) {
                    animals
                } else {
                    animals.filter {
                        it.name.contains(charSearch, ignoreCase = true)
                    }
                }
                return FilterResults().apply { values = animalsFiltered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                animalsFiltered = results?.values as List<Animal>
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
        notifyDataSetChanged()
    }
}