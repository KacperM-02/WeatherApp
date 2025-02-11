package com.example.weatherapp.data.model

data class WeatherResponse(
    val coord: Coord,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val visibility: Int,
    val name: String,
    val dt: Long,
    val sys: Sys
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)