package com.example.todolist

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Code from https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-662e4d1794b
data class CalendarUiModel(
    val selectedDate: Date,
    val visibleDates: List<Date>
) {

    val startDate: Date = visibleDates.first()
    val endDate: Date = visibleDates.last()

    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        @RequiresApi(Build.VERSION_CODES.O)
        val day: String = date.format(DateTimeFormatter.ofPattern("E"))
    }
}