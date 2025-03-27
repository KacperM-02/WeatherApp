package com.example.weatherapp.data.preferences

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherSettingsPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("weather_settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun loadFavoriteCities(): List<String> {
        val citiesJson = preferences.getString("favorite_cities", "[]")
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(citiesJson, type)
    }

    fun addFavoriteCity(city: String) {
        val citiesList = loadFavoriteCities().toMutableList()
        citiesList.add(city)

        val citiesJson = gson.toJson(citiesList)
        preferences.edit()
            .putString("favorite_cities", citiesJson)
            .apply()
    }

    fun removeFavoriteCity(city: String) {
        val citiesList = loadFavoriteCities().toMutableList()
        citiesList.remove(city)

        val citiesJson = gson.toJson(citiesList)
        preferences.edit()
            .putString("favorite_cities", citiesJson)
            .apply()
    }

    fun isCityFavorite(city: String): Boolean {
        val citiesList = loadFavoriteCities()
        return citiesList.contains(city)
    }

    fun getUnits(): String {
        return preferences.getString("units", "metric") ?: "metric"
    }

    fun saveUnits(units: String) {
        preferences.edit()
            .putString("units", units)
            .apply()
    }
}
