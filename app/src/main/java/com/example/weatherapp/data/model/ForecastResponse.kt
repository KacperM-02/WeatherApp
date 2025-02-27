package com.example.weatherapp.data.model

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
)

data class City(
    val name: String,
    val country: String
)