package com.example.todolist.ToDoList.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.Stream

// Code from https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-662e4d1794b
class CalendarDataSource {
    val today: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() { return LocalDate.now() }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(startDate: LocalDate = today, lastSelectedDate: LocalDate): CalendareDataClass {
        val firstDayOfWeek = startDate.with(DayOfWeek.MONDAY)
        val endDayOfWeek = firstDayOfWeek.plusDays(7)
        val visibleDates = getDatesBetween(firstDayOfWeek, endDayOfWeek)
        return toUiModel(visibleDates, lastSelectedDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val numOfDays = ChronoUnit.DAYS.between(startDate, endDate)
        return Stream.iterate(startDate) { date -> date.plusDays(1) }
            .limit(numOfDays)
            .collect(Collectors.toList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toUiModel(
        dateList: List<LocalDate>,
        lastSelectedDate: LocalDate
    ): CalendareDataClass {
        return CalendareDataClass(
            selectedDate = toItemUiModel(lastSelectedDate, true),
            visibleDates = dateList.map {
                toItemUiModel(it, it.isEqual(lastSelectedDate))
            },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toItemUiModel(date: LocalDate, isSelectedDate: Boolean) = CalendareDataClass.CalendarDate(
        isSelected = isSelectedDate,
        isToday = date.isEqual(today),
        date = date,
    )
}