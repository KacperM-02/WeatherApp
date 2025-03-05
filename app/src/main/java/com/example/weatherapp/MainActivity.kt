package com.example.weatherapp

import android.content.Context
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
import com.example.weatherapp.data.api.WeatherApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.weatherapp.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherPreferences: WeatherPreferences
    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

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
        fetchInitialData()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun fetchInitialData() {
        val lastFetchTime = weatherPreferences.getWeatherTimestamp()
        val currentTime = System.currentTimeMillis()
        val isInternetAvailable = isInternetAvailable()
        val weatherPreferences = weatherPreferences.getWeatherResponse()

        if (isInternetAvailable) {
            if (currentTime - lastFetchTime > 15 * 60 * 1000) {
                saveData()
                return
            }
        }

        if (isInternetAvailable) {
            if (weatherPreferences == null) saveData()
            return
        }
        Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show()

        if (weatherPreferences == null)
        {
            Toast.makeText(this, "No data available.", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentTime - lastFetchTime > 15 * 60 * 1000) Toast.makeText(this, "Data is outdated.", Toast.LENGTH_SHORT).show()
    }

    private fun saveData()
    {
        lifecycleScope.launch {
            try {
                val weatherResponse = weatherApi.getWeather(756135, BuildConfig.API_KEY)
                weatherPreferences.saveWeatherResponse(weatherResponse)

                val forecastResponse = weatherApi.getForecast(756135, BuildConfig.API_KEY)
                weatherPreferences.saveForecastResponse(forecastResponse)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
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