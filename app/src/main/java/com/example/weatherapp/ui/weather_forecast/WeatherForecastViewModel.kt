package com.example.weatherapp.ui.weather_forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.ForecastResponse


class WeatherForecastViewModel: ViewModel() {
    private val _forecast = MutableLiveData<List<ForecastItem>>()
    val forecast: LiveData<List<ForecastItem>> = _forecast

    private val _weatherIconsList = MutableLiveData<List<ByteArray>>()
    val weatherIcon: LiveData<List<ByteArray>> = _weatherIconsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun updateForecastData(response: ForecastResponse) {
        _forecast.value = response.list
    }

    fun updateWeatherIcon(iconBytesList: List<ByteArray>) {
        _weatherIconsList.value = iconBytesList
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