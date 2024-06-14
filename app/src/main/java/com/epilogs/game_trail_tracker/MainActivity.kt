package com.epilogs.game_trail_tracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.epilogs.game_trail_tracker.database.entities.UserSettings
import com.epilogs.game_trail_tracker.viewmodels.UserSettingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private var userSettings = UserSettings(id = 1, theme = "light_mode", measurement = "cm", weight = "kg")
    private lateinit var navController: NavController

    private val userSettingsViewModel: UserSettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        userSettingsViewModel.getUserSettingsById(1).observe(this) { userSetting ->
            when (userSetting?.theme) {
                "dark_mode" -> setTheme(R.style.Base_Theme_Gametrailtracker) // Dark theme
                "light_mode" -> setTheme(R.style.Base_Theme_Gametrailtracker) // Light theme
            }
            if (userSetting == null) {
                userSettingsViewModel.insertUserSettings(userSettings)
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply window insets to the main view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnApplyWindowInsetsListener(null)
        navView.setPadding(0, 0, 0, 0)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.huntFragment -> {
                    navigateToRootFragment(R.id.huntFragment)
                    true
                }
                R.id.trophyFragment -> {
                    navigateToRootFragment(R.id.trophyFragment)
                    true
                }
                R.id.weaponFragment -> {
                    navigateToRootFragment(R.id.weaponFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToRootFragment(fragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(navController.graph.startDestinationId, inclusive = true) // Inclusive to ensure the back stack is cleared
            .build()

        navController.navigate(fragmentId, null, navOptions)
    }
}