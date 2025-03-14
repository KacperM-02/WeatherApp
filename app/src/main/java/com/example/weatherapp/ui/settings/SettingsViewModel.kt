package com.example.weatherapp.ui.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.preferences.WeatherPreferences
import com.example.weatherapp.data.preferences.WeatherSettingsPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherSettingsPreferences = WeatherSettingsPreferences(application)
    private val weatherPreferences = WeatherPreferences(application)
    private val _favoriteCities = MutableLiveData<List<String>>()
    val favoriteCities: LiveData<List<String>> = _favoriteCities
    private val _chosenCityId = MutableLiveData<Int>()
    val chosenCityId: LiveData<Int> = _chosenCityId

    init {
        _favoriteCities.value = weatherSettingsPreferences.loadFavoriteCities()
        _chosenCityId.value = weatherPreferences.getCityId()
    }

    fun addFavoriteCity(city: String) {
        val currentCities = _favoriteCities.value?.toMutableList() ?: mutableListOf()
        if (!currentCities.contains(city)) {
            currentCities.add(city)
            saveFavoriteCities(currentCities)
        }
    }

    fun removeFavoriteCity(city: String) {
        val currentCities = _favoriteCities.value?.toMutableList() ?: mutableListOf()
        if (currentCities.contains(city)) {
            currentCities.remove(city)
            saveFavoriteCities(currentCities)
        }
    }

    private fun saveFavoriteCities(cities: List<String>) {
        viewModelScope.launch {
            weatherSettingsPreferences.saveFavoriteCities(cities)
            _favoriteCities.value = cities
        }
    }

    fun isCityFavorite(city: String): Boolean {
        return _favoriteCities.value?.contains(city) == true
    }

    fun setChosenCityId(cityId: Int) {
        _chosenCityId.value = cityId
        Log.d("SettingsViewModel", "setChosenCityId: Chosen city ID: $cityId")
    }
}