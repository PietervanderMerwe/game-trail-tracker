package com.epilogs.game_trail_tracker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.epilogs.game_trail_tracker.database.entities.UserSettings
import com.epilogs.game_trail_tracker.viewmodels.UserSettingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private var userSettings = UserSettings(id = 1, theme = "light_mode", measurement = "cm", weight = "kg")

    private val userSettingsViewModel: UserSettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnApplyWindowInsetsListener(null)
        navView.setPadding(0,0,0,0)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
        userSettingsViewModel.getUserSettingsById(1).observe(this) { userSetting ->
            if(userSetting == null) {
                userSettingsViewModel.insertUserSettings(userSettings)
            }
        }
    }
}