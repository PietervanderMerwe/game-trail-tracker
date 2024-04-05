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
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.interfaces.OnWeaponItemClickListener

class WeaponViewAdapter(
    private var weapons: List<Weapon>,
    private val listener: OnWeaponItemClickListener
) : RecyclerView.Adapter<WeaponViewAdapter.WeaponViewHolder>(),
    Filterable {

    private var weaponsFiltered = weapons
    private var currentSearchText: String? = ""

    class WeaponViewHolder(view: View, private val listener: OnWeaponItemClickListener) :
        RecyclerView.ViewHolder(view) {
        fun bind(weapon: Weapon) {
            itemView.findViewById<TextView>(R.id.weapon_view_item_name).text = weapon.name
            itemView.findViewById<TextView>(R.id.weapon_view_item_notes).text = weapon.notes

            weapon.imagePaths?.let {
                if (it.isNotEmpty()) {
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.weapon_view_item_image))
                }
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onWeaponItemClick(weapon)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                weaponsFiltered = if (charSearch.isEmpty()) {
                    weapons
                } else {
                    weapons.filter {
                        it.name.contains(charSearch, ignoreCase = true)
                    }
                }
                return FilterResults().apply { values = weaponsFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                weaponsFiltered = results?.values as? List<Weapon> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weapon_view_item, parent, false)
        return WeaponViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: WeaponViewHolder, position: Int) {
        holder.bind(weaponsFiltered[position])
    }

    override fun getItemCount() = weaponsFiltered.size

    fun updateLocations(newWeapons: List<Weapon>) {
        weapons = newWeapons
        weaponsFiltered = newWeapons
        applyFilter()
    }

    private fun applyFilter() {
        filter.filter(currentSearchText)
    }
}