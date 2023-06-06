package com.example.weather_app

data class WeatherResponse(val main: WeatherMain,
                           val weather: List<Weather>
                           )

data class WeatherMain(
    val temp: Double,
    val humidity: Int
)

data class Weather(
    val main: String,
    val description: String
)
