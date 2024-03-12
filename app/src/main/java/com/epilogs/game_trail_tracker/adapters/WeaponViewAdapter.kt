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
import com.epilogs.game_trail_tracker.database.entities.Weapon
import java.text.SimpleDateFormat
import java.util.Locale

class WeaponViewAdapter (private var weapons: List<Weapon>) : RecyclerView.Adapter<WeaponViewAdapter.WeaponViewHolder>() {

    class WeaponViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(weapon: Weapon) {
            itemView.findViewById<TextView>(R.id.weapon_view_item_name).text = weapon.name
            itemView.findViewById<TextView>(R.id.weapon_view_item_notes).text = weapon.notes

            weapon.imagePaths?.let {
                if (it.isNotEmpty()) {
                    // Assuming you're using Glide or a similar library to load images
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.weapon_view_item_image))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weapon_view_item, parent, false)
        return WeaponViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeaponViewHolder, position: Int) {
        holder.bind(weapons[position])
    }

    override fun getItemCount() = weapons.size

    fun updateLocations(newWeapons: List<Weapon>) {
        weapons = newWeapons
        notifyDataSetChanged()
    }
}