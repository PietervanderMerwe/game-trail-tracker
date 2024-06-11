package com.epilogs.game_trail_tracker.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.bumptech.glide.Glide
class ImagesAdapter(private val images: MutableList<String>, private val itemClickListener: (String, Int) -> Unit) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageItemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = images[position]
        Glide.with(holder.imageView.context).load(imagePath).into(holder.imageView)
        holder.itemView.setOnClickListener {
            itemClickListener(imagePath, position)
        }
    }

    override fun getItemCount() = images.size

    fun updateImages(newImages: List<String>) {
        val startSize = images.size
        newImages.forEach { newImage ->
            if (!images.contains(newImage)) {
                images.add(newImage)
                notifyItemInserted(images.size - 1)
            }
        }
        if (startSize != images.size) {
            notifyItemRangeChanged(startSize, images.size - startSize)
        }
    }
    fun clearImages() {
        val itemCount = images.size
        images.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

}
