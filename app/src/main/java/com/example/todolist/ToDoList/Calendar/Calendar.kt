package com.example.todolist.ToDoList.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.ui.theme.Purple60
import com.example.todolist.ui.theme.Purple80
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Code adapted from https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-662e4d1794b
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarHeader(
    data: CalendareDataClass,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            IconButton(onClick = { onPrevClickListener(data.startDate.date) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous"
                )
            }
            IconButton(onClick = { onNextClickListener(data.endDate.date) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next"
                )
            }
        }
        Text(
            text = if (data.selectedDate.isToday) { "Today" }
            else {
                data.selectedDate.date.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                )
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 10.dp),
            style = TextStyle(fontSize = 18.sp),
            textAlign = TextAlign.Right,
        )
    }
}

// Code adapted from https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-662e4d1794b
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarItem(
    date: CalendareDataClass.CalendarDate,
    onClickListener: (CalendareDataClass.CalendarDate) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable { onClickListener(date) },
        colors = CardDefaults.cardColors(
            containerColor = if (date.isSelected) { MaterialTheme.colorScheme.primary }
            else if (date.isToday) { Purple60 }
            else { MaterialTheme.colorScheme.primaryContainer }
        ),
    ) {
        Column(
            modifier = Modifier
                .width(43.dp)
                .height(63.dp)
                .padding(4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                color = if (date.isSelected || date.isToday) { Color.White }
                else { Color.Black }
            )
            Text(
                text = date.day,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall,
                color = if (date.isSelected || date.isToday) { Color.White }
                else { Color.Black }
            )
        }
    }
}

// Code adapted from https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-662e4d1794b
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
    data: CalendareDataClass,
    onDateClickListener: (CalendareDataClass.CalendarDate) -> Unit,
) {
    LazyRow {
        items(items = data.visibleDates) { date ->
            CalendarItem(date = date, onDateClickListener)
        }
    }
}