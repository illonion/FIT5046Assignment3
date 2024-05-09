package com.example.todolist.ToDoList

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolist.DatabaseActivity
import com.example.todolist.LoginSignup.AuthenticationActivity
import com.example.todolist.Navigation.Routes
import com.example.todolist.ToDoList.Calendar.Calendar
import com.example.todolist.ToDoList.Calendar.CalendarDataSource
import com.example.todolist.ToDoList.Calendar.CalendarHeader
import com.example.todolist.ToDoList.Calendar.CalendareDataClass
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoList(navController: NavHostController, viewModel: ToDoListItemViewModel) {
    TopAppBar(
        title = { Text(text = "Task List") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
    ToDoListContent(navController = navController, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ToDoListContent(navController: NavHostController, viewModel: ToDoListItemViewModel) {
    val completeOptions = listOf("All", "Not Completed", "Completed")
    val tagOptions = listOf("All", "No Tag", "Indoors", "Outdoors", "School", "Work", "Sports")

    var completeIsExpanded by remember { mutableStateOf(false) }
    var selectedComplete by remember { mutableStateOf(completeOptions[0]) }

    var tagsIsExpanded by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf(tagOptions[0]) }

    val dataSource = CalendarDataSource()
    var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.syncDataFromFirebase()

        // Check if user logged in another device every 5 seconds
        while(true) {
            DatabaseActivity().checkValidSession(context) { isValidSession ->
                if (!isValidSession) {
                    navController.navigate(Routes.MainLogout.value)
                }
            }
            delay(5000)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Calendar Section
        Spacer(modifier = Modifier.height(60.dp))
        CalendarHeader(
            data = calendarUiModel,
            onPrevClickListener = { calendarUiModel = dataSource.getData(startDate = it.minusDays(1), lastSelectedDate = calendarUiModel.selectedDate.date) },
            onNextClickListener = { calendarUiModel = dataSource.getData(startDate = it.plusDays(1), lastSelectedDate = calendarUiModel.selectedDate.date) }
        )
        Calendar(data = calendarUiModel, onDateClickListener = { calendarUiModel = calendarUiModel.copy(selectedDate = it, visibleDates = calendarUiModel.visibleDates.map { date -> date.copy(isSelected = date.date.isEqual(it.date)) }) })

        // Add new item (CreateToDoListItem.kt)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { navController.navigate(Routes.CreateToDoListItem.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text("Add new task")
        }

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
                ).width(90.dp)
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
                    completeOptions.forEach {
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

        // Tag filtering
        Row {
            Text(
                text = "Tags: ",
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
                ).width(90.dp)
            )
            ExposedDropdownMenuBox(
                expanded = tagsIsExpanded,
                onExpandedChange = { tagsIsExpanded = it },
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
                        Text(selectedTag)
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagsIsExpanded)
                    }
                }
                ExposedDropdownMenu(
                    expanded = tagsIsExpanded,
                    onDismissRequest = { tagsIsExpanded = false }
                ) {
                    tagOptions.forEach {
                        selectedOption -> DropdownMenuItem(
                            text = { Text(selectedOption) },
                            onClick = {
                                selectedTag = selectedOption
                                tagsIsExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }

        // To Do list items
        Spacer(modifier = Modifier.height(16.dp))
        ToDoListItems(viewModel, calendarUiModel, selectedComplete, selectedTag)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ToDoListItems(
    viewModel: ToDoListItemViewModel,
    calendarUiModel: CalendareDataClass,
    selectedComplete: String,
    selectedTag: String
) {
    var toDoListItems by remember { mutableStateOf(emptyList<ToDoListItem>()) }
    val currentUserUid = AuthenticationActivity().getUser()?.uid

    // In order:
    // Filter by tasks where the user is involved
    // Filter by due date being the date currently selected
    // Filter by completeness
    // Filter by tag name
    // Sort by the date the task was created
    // Make it into a list
    viewModel.allToDoListItems.observeAsState(emptyList()).apply {
        toDoListItems = this.value
            .asSequence()
            .filter { it.userId == currentUserUid || it.friend == currentUserUid }
            .filter { LocalDate.parse(it.dueDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")) == calendarUiModel.selectedDate.date }
            .filter {
                when (selectedComplete) {
                    "Not Completed" -> !it.completed
                    "Completed" -> it.completed
                    else -> true
                }
            }
            .filter { it.tag == selectedTag || selectedTag == "All" }
            .sortedBy { it.createdAt }
            .toList()
    }

    // If there are no to do list items
    if (toDoListItems.isEmpty()) {
        Text(text = "There are currently no items on this day's task list!")
    } else {
        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            itemsIndexed(toDoListItems) { _, item ->
                ListToDoListItem(item, true, viewModel, null)
            }
        }
    }
}