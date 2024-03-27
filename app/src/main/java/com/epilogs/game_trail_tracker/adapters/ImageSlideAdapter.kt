package com.epilogs.game_trail_tracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epilogs.game_trail_tracker.R
import com.github.chrisbanes.photoview.PhotoView

class ImageSlideAdapter(
    val context: Context,
    private val imageUrls: ArrayList<String>
) : RecyclerView.Adapter<ImageSlideAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoView: PhotoView = view.findViewById(R.id.photo_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image_slide, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(context).load(imageUrl).into(holder.photoView)
    }

    override fun getItemCount(): Int = imageUrls.size
}