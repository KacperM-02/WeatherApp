package com.example.weatherapp.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.api.WeatherApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    fun fetchWeatherData(city: String = "Warsaw") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = weatherApi.getWeather(city, BuildConfig.API_KEY)

                val windDirection = getWindDirection(response.wind.deg)
                
                _weatherData.value = """
                    Wiatr:
                    • Prędkość: ${response.wind.speed} m/s
                    • Kierunek: $windDirection (${response.wind.deg}°)
                    • Porywy: ${response.wind.gust ?: "brak danych"} m/s
                    
                    Wilgotność: ${response.main.humidity}%
                    Widoczność: ${response.visibility / 1000.0} km
                """.trimIndent()
            } catch (e: Exception) {
                _error.value = "Błąd: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
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