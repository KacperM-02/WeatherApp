package com.example.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.api.WeatherApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class HomeViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> = _weatherData

    private val _weatherIcon = MutableLiveData<ByteArray>()
    val weatherIcon: LiveData<ByteArray> = _weatherIcon

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    private val iconUrl = Retrofit.Builder()
        .baseUrl("https://openweathermap.org/")
        .build()
        .create(WeatherApi::class.java)

    fun fetchWeatherData(city: String = "Warsaw") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = weatherApi.getWeather(city, BuildConfig.API_KEY)
                
                response.weather.firstOrNull()?.icon?.let { icon ->
                    try {
                        val iconResponse = iconUrl.getWeatherIcon(icon)
                        _weatherIcon.value = iconResponse.bytes()
                    } catch (e: Exception) {
                        _error.value = "Błąd pobierania ikony: ${e.message}"
                    }
                }

                val time = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(response.dt * 1000))

                _weatherData.value = """
                    Miasto: ${response.name}, ${response.sys.country}
                    Współrzędne: ${response.coord.lat}°N, ${response.coord.lon}°E
                    Czas: $time
                    
                    Temperatura: ${response.main.temp}°C
                    Odczuwalna: ${response.main.feels_like}°C
                    Ciśnienie: ${response.main.pressure} hPa
                    
                    Pogoda: ${response.weather.firstOrNull()?.description ?: ""}
                """.trimIndent()
            } catch (e: Exception) {
                _error.value = "Błąd: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}