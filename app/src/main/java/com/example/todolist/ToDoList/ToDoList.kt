package com.example.todolist.ToDoList

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolist.Navigation.Routes
import com.example.todolist.ToDoList.Calendar.Calendar
import com.example.todolist.ToDoList.Calendar.CalendarDataSource
import com.example.todolist.ToDoList.Calendar.CalendarHeader
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoList(navController: NavHostController, viewModel: ToDoListItemViewModel) {
    var completeIsExpanded by remember { mutableStateOf(false) }
    val complete = listOf("Not Completed", "Completed", "All")
    var selectedComplete by remember { mutableStateOf(complete[0]) }


    LaunchedEffect(Unit) {
        viewModel.syncDataFromFirebase()
    }

    // Top bar
    TopAppBar(
        title = { Text(text = "Task List") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
            MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )

    // Column from https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-662e4d1794b
    val dataSource = CalendarDataSource()
    var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(60.dp))
        CalendarHeader(
            data = calendarUiModel,
            onPrevClickListener = { startDate ->
                // refresh the CalendarUiModel with new data
                // by get data with new Start Date (which is the startDate-1 from the visibleDates)
                val finalStartDate = startDate.minusDays(1)
                calendarUiModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarUiModel.selectedDate.date)
            },
            onNextClickListener = { endDate ->
                // refresh the CalendarUiModel with new data
                // by get data with new Start Date (which is the endDate+2 from the visibleDates)
                val finalStartDate = endDate.plusDays(2)
                calendarUiModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarUiModel.selectedDate.date)
            }
        )
        Calendar(data = calendarUiModel, onDateClickListener = { date ->
            // refresh the CalendarUiModel with new data
            // by changing only the `selectedDate` with the date selected by User
            calendarUiModel = calendarUiModel.copy(
                selectedDate = date,
                visibleDates = calendarUiModel.visibleDates.map {
                    it.copy(
                        isSelected = it.date.isEqual(date.date)
                    )
                }
            )
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(190.dp))
        // Add new item (CreateToDoListItem.kt)
        Button(
            onClick = { navController.navigate(Routes.CreateToDoListItem.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text("Add new task")
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Filter by completeness
        Row {
            Text(
                text = "Complete: ",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 32.sp
                ),
                modifier = Modifier.padding(
                    PaddingValues(
                        start = 4.dp,
                        top = 8.dp,
                        end = 4.dp,
                        bottom = 8.dp
                    )
                )
            )
            ExposedDropdownMenuBox(
                expanded = completeIsExpanded,
                onExpandedChange = { completeIsExpanded = it },
                modifier = Modifier
                    .width(135.dp)
                    .height(32.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(32.dp))
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(32.dp))
            ) {
                Box(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .focusProperties { canFocus = false },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(selectedComplete)
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = completeIsExpanded)
                    }
                }
                ExposedDropdownMenu(
                    expanded = completeIsExpanded,
                    onDismissRequest = { completeIsExpanded = false }
                ) {
                    complete.forEach {
                        selectedOption -> DropdownMenuItem(
                            text = { Text(selectedOption) },
                            onClick = {
                                selectedComplete = selectedOption
                                completeIsExpanded = false
                            },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }


        // Initialise to do list items
        var toDoListItems by remember { mutableStateOf(emptyList<ToDoListItem>()) }
        // Get current User ID
        val currentUserUid = Firebase.auth.currentUser?.uid
        // Get and filter to do list items
        viewModel.allToDoListItems.observeAsState(emptyList()).apply {
            toDoListItems = this.value
                .filter { it.userId == currentUserUid || it.friend == currentUserUid } // Remove items where the user is not the user OR a friend
                .filter {
                    LocalDate.parse(it.dueDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")) == calendarUiModel.selectedDate.date
                } // Filter by the due date that is currently selected
                .filter {
                    when (selectedComplete) {
                        "Not Completed" -> !it.completed
                        "Completed" -> it.completed
                        else -> true // "All" selected, include all items
                    }
                } // Filter by completed or not completed
                .sortedBy { it.createdAt } // Sort items by creation date
        }

        // Check if the current list is empty
        if (toDoListItems.isEmpty()) {
            Text(text = "There are currently no items on this day's task list!")
        } else {
            // Column of to do list items
            Column (
                modifier = Modifier.padding(top = 8.dp)
            ) {
                LazyColumn {
                    itemsIndexed(toDoListItems) { _, item ->
                        ListToDoListItem(item, true, viewModel)
                    }
                }
            }
        }
    }
}