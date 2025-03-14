package com.example.weatherapp.data.model

data class WeatherResponse(
    val coord: Coord,
    val main: Main,
    val weather: List<Weather>,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val id: Int,
    val name: String,
)

class Clouds {
    val all: Int = 0
}

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Main(
    val temp: Double,
    val feelsLike: Double,
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