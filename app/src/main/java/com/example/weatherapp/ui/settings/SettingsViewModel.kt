package com.example.weatherapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("weather_settings", Application.MODE_PRIVATE)

    fun saveSettings(city: String, units: String) {
        viewModelScope.launch {
            preferences.edit()
                .putString("city", city)
                .putString("units", units)
                .apply()
        }
    }

    fun getCity(): String? {
        return preferences.getString("city", null)
    }

    fun getUnits(): String {
        return preferences.getString("units", "metric") ?: "metric"
    }
} 