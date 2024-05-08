package com.example.todolist.Weather.data.models

// https://github.com/MohsenMashkour/WeatherAppExample
data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)