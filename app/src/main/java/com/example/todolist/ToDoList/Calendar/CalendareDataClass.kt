package com.example.todolist.ToDoList.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Code from https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-662e4d1794b
data class CalendareDataClass(
    val selectedDate: CalendarDate,
    val visibleDates: List<CalendarDate>
) {

    val startDate: CalendarDate = visibleDates.first()
    val endDate: CalendarDate = visibleDates.last()

    data class CalendarDate(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        @RequiresApi(Build.VERSION_CODES.O)
        val day: String = date.format(DateTimeFormatter.ofPattern("E"))
    }
}