package com.epilogs.game_trail_tracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.epilogs.game_trail_tracker.adapters.ImageSlideAdapter

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageUrls = intent.getStringArrayListExtra("image_urls") ?: arrayListOf()
        val startPosition = intent.getIntExtra("image_position", 0)

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = ImageSlideAdapter(this, imageUrls)
        viewPager.setCurrentItem(startPosition, false)

        // Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_color)
    }
}