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

    fun updateWeatherData(response: WeatherResponse) {
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