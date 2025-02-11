package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.WeatherResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("img/wn/{icon}@2x.png")
    suspend fun getWeatherIcon(
        @Path("icon") icon: String
    ): ResponseBody
} 