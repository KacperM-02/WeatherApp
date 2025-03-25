package com.example.weatherapp

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.data.preferences.WeatherPreferences
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.weatherapp.ui.settings.SettingsActivity
import com.example.weatherapp.ui.weather_data.WeatherDataViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherPreferences: WeatherPreferences
    private val weatherDataViewModel : WeatherDataViewModel by viewModels()
    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val chosenCityId = result.data?.getIntExtra("chosenCityId", -1)
            if (chosenCityId != null && chosenCityId != -1) {
                weatherDataViewModel.fetchWeatherData(chosenCityId)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        weatherPreferences = WeatherPreferences(this)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.weather_data, R.id.weather_details, R.id.weather_forecast
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_settings -> {
                settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
                || super.onSupportNavigateUp()
    }
}