package com.example.todolist.Weather

import com.example.todolist.Weather.data.models.CurrentWeather
import com.example.todolist.Weather.util.ApiKeys
import com.example.todolist.Weather.util.RetrofitInstance

class ItemsRepository {

    private val apiService = RetrofitInstance.api

    suspend fun getCurrentWeather(city: String, units: String): CurrentWeather? {
        val response = apiService.getCurrentWeather(city, units, ApiKeys.API_KEY)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}