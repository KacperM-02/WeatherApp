package com.example.weatherapp.ui.home

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
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
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

    private val iconUrl = Retrofit.Builder()
        .baseUrl("https://openweathermap.org/")
        .build()
        .create(WeatherApi::class.java)

    fun fetchWeatherData(cityId: Int = 756135) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Najpierw spróbuj użyć zapisanych danych
                weatherPreferences.getWeatherResponse()?.let { response ->
                    updateWeatherData(response)
                    // Jeśli dane są starsze niż 15 minut, pobierz nowe
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
        response.weather.firstOrNull()?.icon?.let { icon ->
            viewModelScope.launch {
                try {
                    val iconResponse = iconUrl.getWeatherIcon(icon)
                    _weatherIcon.value = iconResponse.bytes()
                } catch (e: Exception) {
                    _error.value = "Błąd pobierania ikony: ${e.message}"
                }
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
    }
}