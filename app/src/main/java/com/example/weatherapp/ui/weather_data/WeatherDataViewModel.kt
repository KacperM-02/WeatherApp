package com.example.weatherapp.ui.weather_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.model.WeatherResponse
import java.util.*

class WeatherDataViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> = _weatherData

    private val _weatherIcon = MutableLiveData<ByteArray>()
    val weatherIcon: LiveData<ByteArray> = _weatherIcon

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun updateWeatherData(response: WeatherResponse, units: String) {
        val time = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(response.dt * 1000))
        val tempUnits = when(units) {
            "standard" -> "K"
            "metric" -> "°C"
            "imperial" -> "°F"
            else -> "°C"
        }

        _weatherData.value = """
            City: ${response.name}, ${response.sys.country}
            Coordinates: ${"%.1f".format(response.coord.lat)}°N, ${"%.1f".format(response.coord.lon)}°E
            Time of data calculation: $time
            
            Temperature: ${"%.1f%s".format(response.main.temp, tempUnits)}
            Feels like: ${"%.1f%s".format(response.main.feels_like, tempUnits)}
            Pressure: ${response.main.pressure} hPa
            
            Description: ${response.weather.firstOrNull()?.description ?: ""}
        """.trimIndent()
    }

    fun updateWeatherIcon(iconBytes: ByteArray) {
        _weatherIcon.value = iconBytes
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