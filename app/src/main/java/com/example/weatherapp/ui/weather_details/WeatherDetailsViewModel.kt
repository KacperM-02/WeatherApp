package com.example.weatherapp.ui.weather_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.model.WeatherResponse

class WeatherDetailsViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

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

    fun updateWeatherData(response: WeatherResponse) {
        val windSpeed = response.wind.speed
        val windDeg = response.wind.deg
        val windDirection = getWindDirection(windDeg)
        val visibility = response.visibility / 1000.0 // konwersja na kilometry
        val clouds = response.clouds.all

        _weatherData.value = """
            Wind speed: $windSpeed m/s
            Wind direction: $windDirection ($windDeg°)
            Visibility: $visibility km
            Clouds: $clouds%
            Humidity: ${response.main.humidity}%
        """.trimIndent()
    }

    fun updateIsLoadingValue(isLoading: Boolean)
    {
        _isLoading.value = isLoading
    }

    fun updateErrorValue(error: String)
    {
        _error.value = error
    }
}