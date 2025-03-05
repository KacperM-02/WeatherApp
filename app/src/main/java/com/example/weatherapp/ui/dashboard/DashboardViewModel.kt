package com.example.weatherapp.ui.dashboard

import android.app.Application
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

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> = _weatherData

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

    fun fetchWeatherData(cityId: Int = 756135) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                weatherPreferences.getWeatherResponse()?.let { response ->
                    updateWeatherData(response)
                    if (System.currentTimeMillis() - weatherPreferences.getWeatherTimestamp() > 15 * 60 * 1000) {
                        fetchFreshData(cityId)
                    }
                } ?: fetchFreshData(cityId)
                
            } catch (e: Exception) {
                _error.value = "Błąd: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchFreshData(cityId: Int) {
        val response = weatherApi.getWeather(cityId, BuildConfig.API_KEY)
        weatherPreferences.saveWeatherResponse(response)
        updateWeatherData(response)
    }

    private fun updateWeatherData(response: WeatherResponse) {
        val windSpeed = response.wind.speed
        val windDeg = response.wind.deg
        val windDirection = getWindDirection(windDeg)
        val visibility = response.visibility / 1000.0 // konwersja na kilometry
        val clouds = response.clouds.all

        _weatherData.value = """
            Wiatr: $windSpeed m/s
            Kierunek wiatru: $windDirection ($windDeg°)
            Widoczność: $visibility km
            Zachmurzenie: $clouds%
            Wilgotność: ${response.main.humidity}%
        """.trimIndent()
    }

    private fun getWindDirection(degrees: Int): String {
        return when (degrees) {
            in 337..360, in 0..22 -> "N"
            in 23..67 -> "NE"
            in 68..112 -> "E"
            in 113..157 -> "SE"
            in 158..202 -> "S"
            in 203..247 -> "SW"
            in 248..292 -> "W"
            in 293..336 -> "NW"
            else -> "N"
        }
    }
}