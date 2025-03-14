package com.example.weatherapp.ui.weather_data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.preferences.WeatherPreferences
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class WeatherDataViewModel(application: Application) : AndroidViewModel(application) {
    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> = _weatherData

    private val _weatherIcon = MutableLiveData<ByteArray>()
    val weatherIcon: LiveData<ByteArray> = _weatherIcon

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val weatherPreferences = WeatherPreferences(application)
    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)
    private val connectivityManager = application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        val lastFetchTime = weatherPreferences.getWeatherTimestamp()
        val currentTime = System.currentTimeMillis()
        val isInternetAvailable = isInternetAvailable()
        val weatherResponse = weatherPreferences.getWeatherResponse()

        if (isInternetAvailable) {
            if (currentTime - lastFetchTime > 15 * 60 * 1000) {
                fetchWeatherData(weatherPreferences.getCityId())
                return
            }

            weatherResponse?.let { updateWeatherData(it) }
            return
        }

        _error.value = "No internet connection."

        if (weatherResponse == null)
        {
            _error.value = "No data available."
            return
        }
        if (currentTime - lastFetchTime > 15 * 60 * 1000) _error.value = "Data is outdated."
    }

    private fun isInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun fetchWeatherData(cityId : Int)
    {
        Log.d("WeatherDataViewModel", "fetchWeatherData(): cityId: $cityId")
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val weatherResponse = weatherApi.getWeather(cityId, BuildConfig.API_KEY)
                weatherPreferences.saveWeatherResponse(weatherResponse)

                updateWeatherData(weatherResponse)

                val forecastResponse = weatherApi.getForecast(cityId, BuildConfig.API_KEY)
                weatherPreferences.saveForecastResponse(forecastResponse)
            } catch (e: Exception) {
                _error.value = "Error fetching data: ${e.message}"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateWeatherData(response: WeatherResponse) {
        val time = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(response.dt * 1000))

        _weatherData.value = """
            City: ${response.name}, ${response.sys.country}
            Coordinates: ${response.coord.lat}°N, ${response.coord.lon}°E
            Time: $time
            
            Temperature: ${response.main.temp}°C
            Feels like: ${response.main.feelsLike}°C
            Pressure: ${response.main.pressure} hPa
            
            Description: ${response.weather.firstOrNull()?.description ?: ""}
        """.trimIndent()
    }
}