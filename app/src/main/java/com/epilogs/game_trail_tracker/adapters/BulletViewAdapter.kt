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
import com.epilogs.game_trail_tracker.database.entities.Bullet
import com.epilogs.game_trail_tracker.interfaces.OnBulletItemClickListener

class BulletViewAdapter(
    private var bullets: List<Bullet>,
    private val listener: OnBulletItemClickListener
) : RecyclerView.Adapter<BulletViewAdapter.BulletViewHolder>(),
    Filterable {

    private var bulletsFiltered = bullets
    private var currentSearchText: String? = ""

    class BulletViewHolder(view: View, private val listener: OnBulletItemClickListener) :
        RecyclerView.ViewHolder(view) {
        fun bind(bullet: Bullet) {
            itemView.findViewById<TextView>(R.id.bullet_view_item_name).text = bullet.bulletBrand
            itemView.findViewById<TextView>(R.id.bullet_view_item_type).text = bullet.type

            bullet.imagePaths?.let {
                if (it.isNotEmpty()) {
                    Glide.with(itemView.context).load(it[0]).into(itemView.findViewById<ImageView>(R.id.bullet_view_item_image))
                }
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onBulletItemClick(bullet)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                bulletsFiltered = if (charSearch.isEmpty()) {
                    bullets
                } else {
                    bullets.filter {
                        it.bulletBrand.contains(charSearch, ignoreCase = true)
                    }
                }
                return FilterResults().apply { values = bulletsFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                bulletsFiltered = results?.values as? List<Bullet> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bullet_view_item, parent, false)
        return BulletViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: BulletViewHolder, position: Int) {
        holder.bind(bulletsFiltered[position])
    }

    override fun getItemCount() = bulletsFiltered.size

    fun updateBullets(newBullets: List<Bullet>) {
        bullets = newBullets
        bulletsFiltered = newBullets
        applyFilter()
    }

    private fun applyFilter() {
        filter.filter(currentSearchText)
    }
}