package com.example.weatherapp.data.preferences

import android.content.Context
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import com.google.gson.Gson

class WeatherPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveWeatherResponse(response: WeatherResponse) {
        preferences.edit()
            .putString("weather_data", gson.toJson(response))
            .putLong("weather_timestamp", System.currentTimeMillis())
            .apply()
    }

    fun saveForecastResponse(response: ForecastResponse) {
        preferences.edit()
            .putString("forecast_data", gson.toJson(response))
            .putLong("forecast_timestamp", System.currentTimeMillis())
            .apply()
    }

    fun getWeatherResponse(): WeatherResponse? {
        val json = preferences.getString("weather_data", null)
        return if (json != null) {
            gson.fromJson(json, WeatherResponse::class.java)
        } else null
    }

    fun getForecastResponse(): ForecastResponse? {
        val json = preferences.getString("forecast_data", null)
        return if (json != null) {
            gson.fromJson(json, ForecastResponse::class.java)
        } else null
    }

    fun getWeatherTimestamp(): Long {
        return preferences.getLong("weather_timestamp", 0)
    }

    fun getForecastTimestamp(): Long {
        return preferences.getLong("forecast_timestamp", 0)
    }
} 