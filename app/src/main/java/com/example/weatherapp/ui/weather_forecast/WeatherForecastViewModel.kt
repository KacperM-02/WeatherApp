package com.example.weatherapp.ui.weather_forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.model.ForecastResponse


class WeatherForecastViewModel: ViewModel() {
    private val _forecast = MutableLiveData<ForecastResponse>()
    val forecast: LiveData<ForecastResponse> = _forecast

    private val _forecastIcons = MutableLiveData<List<ByteArray>>()
    val forecastIcons: LiveData<List<ByteArray>> = _forecastIcons

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var _tempUnits = MutableLiveData<String>()
    val tempUnits: LiveData<String> = _tempUnits

    fun updateForecastData(response: ForecastResponse, units: String) {
        _forecast.value = response
        _tempUnits.value = units
    }

    fun updateForecastIcons(forecastIcons: List<ByteArray>) {
        this._forecastIcons.value = forecastIcons
    }

    fun updateIsLoadingValue(isLoading: Boolean)
    {
        _isLoading.value = isLoading
    }
}