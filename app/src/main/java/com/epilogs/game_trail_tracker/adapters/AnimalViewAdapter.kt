package com.epilogs.game_trail_tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Animal
import java.text.SimpleDateFormat
import java.util.Locale

class AnimalViewAdapter(private var animals: List<Animal>) : RecyclerView.Adapter<AnimalViewAdapter.AnimalViewHolder>() {

    class AnimalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.animal_view_item, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.bind(animals[position])
    }

    override fun getItemCount() = animals.size

    fun updateAnimals(newAnimals: List<Animal>) {
        animals = newAnimals
        notifyDataSetChanged()
    }
}