package com.example.todolist.Weather.data.models

// https://github.com/MohsenMashkour/WeatherAppExample
data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)