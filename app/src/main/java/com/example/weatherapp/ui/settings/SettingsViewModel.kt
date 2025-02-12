package com.example.weatherapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("weather_settings", Application.MODE_PRIVATE)
    private val gson = Gson()
    private val _favoriteCities = MutableLiveData<List<String>>()
    val favoriteCities: LiveData<List<String>> = _favoriteCities

    init {
        loadFavoriteCities()
    }

    private fun loadFavoriteCities() {
        val citiesJson = preferences.getString("favorite_cities", "[]")
        val type = object : TypeToken<List<String>>() {}.type
        _favoriteCities.value = gson.fromJson(citiesJson, type)
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
            val citiesJson = gson.toJson(cities)
            preferences.edit()
                .putString("favorite_cities", citiesJson)
                .apply()
            _favoriteCities.value = cities
        }
    }

    fun isCityFavorite(city: String): Boolean {
        return _favoriteCities.value?.contains(city) == true
    }

    fun getUnits(): String {
        return preferences.getString("units", "metric") ?: "metric"
    }

    fun saveUnits(units: String) {
        viewModelScope.launch {
            preferences.edit()
                .putString("units", units)
                .apply()
        }
    }
} 