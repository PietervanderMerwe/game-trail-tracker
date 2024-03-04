package com.epilogs.game_trail_tracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val iv_mountain: ImageView = findViewById(R.id.iv_mountain)
        iv_mountain.alpha = 0f
        iv_mountain.animate().setDuration(1500).alpha(1f).withEndAction() {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN,android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}