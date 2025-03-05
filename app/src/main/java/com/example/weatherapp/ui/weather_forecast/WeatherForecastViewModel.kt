package com.example.weatherapp.ui.weather_forecast

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.api.WeatherApi
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.preferences.WeatherPreferences
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherForecastViewModel(application: Application) : AndroidViewModel(application) {
    private val _forecast = MutableLiveData<List<ForecastItem>>()
    val forecast: LiveData<List<ForecastItem>> = _forecast

    private val _weatherIcon = MutableLiveData<Pair<String, ByteArray>>()
    val weatherIcon: LiveData<Pair<String, ByteArray>> = _weatherIcon

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

    fun fetchForecast(cityId: Int = 756135) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Najpierw spróbuj użyć zapisanych danych
                weatherPreferences.getForecastResponse()?.let { response ->
                    updateForecastData(response)
                    // Jeśli dane są starsze niż 15 minut, pobierz nowe
                    if (System.currentTimeMillis() - weatherPreferences.getForecastTimestamp() > 15 * 60 * 1000) {
                        fetchFreshForecast(cityId)
                    }
                } ?: fetchFreshForecast(cityId)

            } catch (e: Exception) {
                _error.value = "Błąd: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchFreshForecast(cityId: Int) {
        val response = weatherApi.getForecast(cityId, BuildConfig.API_KEY)
        weatherPreferences.saveForecastResponse(response)
        updateForecastData(response)
    }

    private fun updateForecastData(response: ForecastResponse) {
        _forecast.value = response.list.filter { 
            response.list.indexOf(it) % 8 == 0 
        }

        response.list.forEach { forecast ->
            forecast.weather.firstOrNull()?.icon?.let { icon ->
                viewModelScope.launch {
                    try {
                        val iconResponse = iconUrl.getWeatherIcon(icon)
                        _weatherIcon.value = icon to iconResponse.bytes()
                    } catch (e: Exception) {
                        _error.value = "Error loading icon: ${e.message}"
                    }
                }
            }
        }
    }
}