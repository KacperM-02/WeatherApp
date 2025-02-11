package com.example.weatherapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.data.model.ForecastItem
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationsViewModel : ViewModel() {
    private val _forecast = MutableLiveData<List<ForecastItem>>()
    val forecast: LiveData<List<ForecastItem>> = _forecast

    private val _weatherIcon = MutableLiveData<Pair<String, ByteArray>>()
    val weatherIcon: LiveData<Pair<String, ByteArray>> = _weatherIcon

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

    fun fetchForecast(city: String = "Warsaw") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = weatherApi.getForecast(city, BuildConfig.API_KEY)
                _forecast.value = response.list.filter { 
                    // Filtruj prognozy co 24h
                    response.list.indexOf(it) % 8 == 0 
                }

                // Pobierz ikony dla każdej prognozy
                response.list.forEach { forecast ->
                    forecast.weather.firstOrNull()?.icon?.let { icon ->
                        try {
                            val iconResponse = iconUrl.getWeatherIcon(icon)
                            _weatherIcon.value = icon to iconResponse.bytes()
                        } catch (e: Exception) {
                            _error.value = "Error loading icon: ${e.message}"
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}