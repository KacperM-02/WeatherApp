package com.example.weatherapp

import android.app.Activity
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
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.ui.settings.SettingsActivity
import com.example.weatherapp.ui.weather_data.WeatherDataViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.ui.weather_details.WeatherDetailsViewModel
import com.example.weatherapp.ui.weather_forecast.WeatherForecastViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherPreferences: WeatherPreferences

    private val weatherDataViewModel : WeatherDataViewModel by viewModels()
    private val weatherDetailsViewModel : WeatherDetailsViewModel by viewModels()
    private val weatherForecastViewModel : WeatherForecastViewModel by viewModels()

    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)


    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val chosenCityId = result.data?.getIntExtra("chosenCityId", -1)
            if (chosenCityId != null && chosenCityId != -1) {
                fetchWeatherData(chosenCityId)
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
        val weatherResponse = weatherPreferences.getWeatherResponse()
        val forecastResponse = weatherPreferences.getForecastResponse()

        if (isInternetAvailable) {
            if (currentTime - lastFetchTime > 15 * 60 * 1000) {
                val cityId = weatherPreferences.getCityId()
                fetchWeatherData(cityId)
                return
            }

            weatherResponse?.let {
                weatherDataViewModel.updateWeatherData(it)
                weatherDetailsViewModel.updateWeatherData(it)
            }

            forecastResponse?.let {
                weatherForecastViewModel.updateForecastData(it)
            }
            return
        }

        Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show()
        weatherDataViewModel.updateErrorValue("No internet connection")
//        weatherDataViewModel.updateErrorValue("No internet connection.")
//        weatherDetailsViewModel.updateErrorValue("No internet connection.")
//        weatherForecastViewModel.updateErrorValue("No internet connection.")

        if (weatherResponse == null)
        {
            Toast.makeText(this, "No data available.", Toast.LENGTH_LONG).show()
//            weatherDataViewModel.updateErrorValue("No data available.")
//            weatherDetailsViewModel.updateErrorValue("No data available.")
//            weatherForecastViewModel.updateErrorValue("No data available.")
        }
        else if (currentTime - lastFetchTime > 15 * 60 * 1000) {
            Toast.makeText(this, "Data is outdated.", Toast.LENGTH_LONG).show()
//            weatherDataViewModel.updateErrorValue("Data is outdated.")
//            weatherDetailsViewModel.updateErrorValue("Data is outdated.")
//            weatherForecastViewModel.updateErrorValue("Data is outdated.")
        }
    }

    private fun fetchWeatherData(chosenCityId : Int) {
        Log.d("WeatherDataViewModel", "fetchWeatherData(): cityId: $chosenCityId")
        lifecycleScope.launch {
            try {
                weatherDataViewModel.updateIsLoadingValue(true)
                weatherDetailsViewModel.updateIsLoadingValue(true)
                weatherForecastViewModel.updateIsLoadingValue(true)

                val weatherResponse = weatherApi.getWeather(chosenCityId, BuildConfig.API_KEY)
                val forecastResponse = weatherApi.getForecast(chosenCityId, BuildConfig.API_KEY)

                weatherPreferences.saveWeatherResponse(weatherResponse)
                weatherPreferences.saveForecastResponse(forecastResponse)

                weatherDataViewModel.updateWeatherData(weatherResponse)
                weatherDetailsViewModel.updateWeatherData(weatherResponse)
                weatherForecastViewModel.updateForecastData(forecastResponse)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error fetching weather data: ${e.message}", Toast.LENGTH_LONG).show()
//                weatherDataViewModel.updateErrorValue("Error fetching weather data: ${e.message}")
//                weatherDetailsViewModel.updateErrorValue("Error fetching weather details: ${e.message}")
//                weatherForecastViewModel.updateErrorValue("Error fetching forecast: ${e.message}")
            }
            finally {
                weatherDataViewModel.updateIsLoadingValue(false)
                weatherDetailsViewModel.updateIsLoadingValue(false)
                weatherForecastViewModel.updateIsLoadingValue(false)
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