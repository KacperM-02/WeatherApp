package com.example.weatherapp.data.preferences

import android.content.Context
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import com.google.gson.Gson
import android.util.Base64
import com.google.gson.reflect.TypeToken

class WeatherPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveWeatherResponse(response: WeatherResponse) {
        preferences.edit()
            .putString("weather_data", gson.toJson(response))
            .putLong("weather_timestamp", System.currentTimeMillis())
            .apply()
        saveCityId(response.id)
    }

    fun getWeatherResponse(): WeatherResponse? {
        val json = preferences.getString("weather_data", null)
        return if (json != null) {
            gson.fromJson(json, WeatherResponse::class.java)
        } else null
    }

    fun getWeatherTimestamp(): Long {
        return preferences.getLong("weather_timestamp", 0)
    }

    fun saveWeatherIcon(icon: ByteArray) {
        val iconBase64 = Base64.encodeToString(icon, Base64.DEFAULT)
        preferences.edit()
            .putString("weather_icon", iconBase64)
            .apply()
    }

    fun getWeatherIcon(): ByteArray? {
        val iconBase64 = preferences.getString("weather_icon", null) ?: return null
        return Base64.decode(iconBase64, Base64.DEFAULT)
    }


    private fun saveCityId(cityId: Int) {
        preferences.edit()
            .putInt("chosen_city_id", cityId)
            .apply()
    }

    fun getCityId(): Int {
        return preferences.getInt("chosen_city_id", 756135)
    }


    fun saveForecastResponse(response: ForecastResponse) {
        preferences.edit()
            .putString("forecast_data", gson.toJson(response))
            .apply()
    }

    fun getForecastResponse(): ForecastResponse? {
        val json = preferences.getString("forecast_data", null)
        return if (json != null) {
            gson.fromJson(json, ForecastResponse::class.java)
        } else null
    }

    fun saveForecastIcons(icons: List<ByteArray>) {
        val iconBase64List = icons.map { icon ->
            Base64.encodeToString(icon, Base64.DEFAULT)
        }
        val jsonIcons = Gson().toJson(iconBase64List)
        preferences.edit()
            .putString("forecast_icons", jsonIcons)
            .apply()
    }

    fun loadForecastIcons(): List<ByteArray>? {
        val jsonIcons = preferences.getString("forecast_icons", null) ?: return null
        val iconBase64List: List<String> = Gson().fromJson(jsonIcons, object : TypeToken<List<String>>() {}.type)

        return iconBase64List.map { base64 ->
            Base64.decode(base64, Base64.DEFAULT)
        }
    }
}
