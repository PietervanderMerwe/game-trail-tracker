package com.epilogs.game_trail_tracker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageUrl = intent.getStringExtra("image_url")

        try {
            val inputStream = contentResolver.openInputStream(Uri.parse(imageUrl))
            // Optionally, use the inputStream to read the content and verify access
            inputStream?.close() // Make sure to close the stream after checking
        } catch (e: Exception) {
            Log.e("FullScreenImageActivity", "Error accessing content URI: ", e)
            // Handle the error or fallback scenario
        }

        val photoView: PhotoView = findViewById(R.id.photo_view)
        Log.d("url in activity", imageUrl.toString())
        Glide.with(this)
            .load(imageUrl.toString())
            .into(photoView)
    }
}