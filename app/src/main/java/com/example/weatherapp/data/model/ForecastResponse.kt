package com.example.weatherapp.data.model

data class ForecastResponse(
    val list: List<ForecastItem>,
)

data class ForecastItem(
    val main: Main,
    val weather: List<Weather>,
)
