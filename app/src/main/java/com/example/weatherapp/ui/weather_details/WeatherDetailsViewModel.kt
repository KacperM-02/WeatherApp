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

    fun updateWeatherData(response: WeatherResponse, units: String) {
        val windSpeed = response.wind.speed
        val windUnit = if (units == "metric" || units == "standard") "m/s" else "mph"
        val windDeg = response.wind.deg
        val windDirection = getWindDirection(windDeg)
        val visibility = response.visibility
        val clouds = response.clouds.all
        val humidity = response.main.humidity

        _weatherData.value = """
            Wind speed: %.1f %s
            Wind direction: %s (%d°)
            Visibility: %d m
            Clouds: %d%%
            Humidity: %d%%
        """.trimIndent().format(windSpeed, windUnit, windDirection, windDeg, visibility, clouds, humidity)
    }

    fun updateIsLoadingValue(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}