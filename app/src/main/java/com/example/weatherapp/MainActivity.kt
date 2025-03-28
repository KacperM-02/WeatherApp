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
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.preferences.WeatherSettingsPreferences
import com.example.weatherapp.ui.weather_details.WeatherDetailsViewModel
import com.example.weatherapp.ui.weather_forecast.WeatherForecastViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherPreferences: WeatherPreferences
    private lateinit var weatherSettingsPreferences: WeatherSettingsPreferences
    private var units = String()

    private val weatherDataViewModel : WeatherDataViewModel by viewModels()
    private val weatherDetailsViewModel : WeatherDetailsViewModel by viewModels()
    private val weatherForecastViewModel : WeatherForecastViewModel by viewModels()

    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)
    private val imageApi = Retrofit.Builder()
        .baseUrl("https://openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val chosenCityId = result.data?.getIntExtra("chosenCityId", -1)
            val units = result.data?.getStringExtra("units")
            if ((units != null && this.units != units) && (chosenCityId != null && chosenCityId != -1)) {
                this.units = units
                fetchWeatherData(chosenCityId)
            } else if (units != null && this.units != units) {
                this.units = units
                fetchWeatherData(weatherPreferences.getCityId())
            } else if (chosenCityId != null && chosenCityId != -1) {
                fetchWeatherData(chosenCityId)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFields()

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


    private fun setupFields() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        weatherPreferences = WeatherPreferences(this)
        weatherSettingsPreferences = WeatherSettingsPreferences(this)
        units = weatherSettingsPreferences.getUnits()
    }

    private fun fetchInitialData() {
        val lastFetchTime = weatherPreferences.getWeatherTimestamp()
        val currentTime = System.currentTimeMillis()
        val isInternetAvailable = isInternetAvailable()

        val weatherResponse = weatherPreferences.getWeatherResponse()
        val weatherIcon = weatherPreferences.getWeatherIcon()

        val forecastResponse = weatherPreferences.getForecastResponse()
        val forecastIcons = weatherPreferences.loadForecastIcons()

        if (isInternetAvailable) {
            if (currentTime - lastFetchTime > 15 * 60 * 1000) {
                fetchWeatherData(weatherPreferences.getCityId())
                return
            }

            updateWeatherData(weatherResponse, weatherIcon, forecastResponse, forecastIcons)
            return
        }

        Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show()

        if (weatherResponse == null || forecastResponse == null || weatherIcon == null || forecastIcons == null || units.isEmpty()) {
            weatherDataViewModel.updateErrorValue("No data available.")
            Toast.makeText(this, "No data available.", Toast.LENGTH_LONG).show()
            return
        }
        if (currentTime - lastFetchTime > 15 * 60 * 1000) Toast.makeText(this, "Data is outdated.", Toast.LENGTH_SHORT).show()

        updateWeatherData(weatherResponse, weatherIcon, forecastResponse, forecastIcons)
    }

    private fun fetchWeatherData(chosenCityId : Int) {
        Log.d("WeatherDataViewModel", "fetchWeatherData(): cityId: $chosenCityId")
        lifecycleScope.launch {
            try {
                updateWeatherLoading(true)

                // Weather
                val weatherResponse = weatherApi.getWeather(chosenCityId, BuildConfig.API_KEY, units)
                val weatherIcon = imageApi.getWeatherIcon(weatherResponse.weather.firstOrNull()?.icon ?: "").bytes()

                // Forecast
                val forecastResponse = weatherApi.getForecast(chosenCityId, BuildConfig.API_KEY, units)

                val filteredList = forecastResponse.list
                    .filter { it.dt_txt.split(" ")[1] == "15:00:00" }
                    .groupBy { it.dt_txt.split(" ")[0] }
                    .map { (_, forecasts) -> forecasts.first() }

                val filteredForecastResponse = forecastResponse.copy(list = filteredList)
                val forecastIcons = filteredForecastResponse.list.map { forecast ->
                    imageApi.getWeatherIcon(forecast.weather.firstOrNull()?.icon ?: "").bytes()
                }

                updateWeatherData(weatherResponse, weatherIcon, filteredForecastResponse, forecastIcons)
                saveWeatherData(weatherResponse, weatherIcon, filteredForecastResponse, forecastIcons)
            } catch (e: Exception) {
                weatherDataViewModel.updateErrorValue("Error fetching weather data: ${e.message}")
                Toast.makeText(this@MainActivity, "Error fetching weather data: ${e.message}", Toast.LENGTH_LONG).show()
            }
            finally {
                updateWeatherLoading(false)
            }
        }
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun updateWeatherData(
        weatherResponse: WeatherResponse?,
        weatherIcon: ByteArray?,
        forecastResponse: ForecastResponse?,
        forecastIcons: List<ByteArray>?,
    ) {
        weatherResponse?.let {
            weatherDataViewModel.updateWeatherData(it, units)
            weatherDetailsViewModel.updateWeatherData(it, units)
        }
        weatherIcon?.let {
            weatherDataViewModel.updateWeatherIcon(weatherIcon)
        }

        forecastResponse?.let {
            weatherForecastViewModel.updateForecastData(it, units)
        }
        forecastIcons?.let {
            weatherForecastViewModel.updateForecastIcons(it)
        }
    }

    private fun updateWeatherLoading(state: Boolean) {
        weatherDataViewModel.updateIsLoadingValue(state)
        weatherDetailsViewModel.updateIsLoadingValue(state)
        weatherForecastViewModel.updateIsLoadingValue(state)
    }

    private fun saveWeatherData(
        weatherResponse: WeatherResponse,
        weatherIcon: ByteArray,
        filteredForecastResponse: ForecastResponse,
        forecastIcons: List<ByteArray>
    ) {
        weatherPreferences.saveWeatherResponse(weatherResponse)
        weatherPreferences.saveWeatherIcon(weatherIcon)
        weatherPreferences.saveForecastResponse(filteredForecastResponse)
        weatherPreferences.saveForecastIcons(forecastIcons)
    }
}