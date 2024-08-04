package com.epilogs.game_trail_tracker.utils

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter

class ImageAdapterSetup(
    private val recyclerView: RecyclerView,
    private val imageUris: MutableSet<String>
) {
    private lateinit var imageAdapter: ImagesAdapter

    fun setupAdapter() {
        imageAdapter = ImagesAdapter(imageUris.toMutableList()) { imageUri, position ->
            startFullScreenImageActivity(imageUri, position)
        }
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun startFullScreenImageActivity(imageUri: String, position: Int) {
        val intent = Intent(recyclerView.context, FullScreenImageActivity::class.java).apply {
            putStringArrayListExtra("image_urls", ArrayList(imageUris.toList()))
            putExtra("image_position", position)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        recyclerView.context.startActivity(intent)
    }

    fun updateImages(newImageUris: List<String>) {
        imageAdapter.updateImages(newImageUris.toSet())
    }

    fun clearImages() {
        imageAdapter.clearImages()
    }
}