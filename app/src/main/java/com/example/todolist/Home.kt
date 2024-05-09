package com.example.todolist

import WeatherScreen
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.todolist.Analytics.AnalyticsViewModel
import com.example.todolist.Analytics.PieChart
import com.example.todolist.Analytics.PieChartInput
import com.example.todolist.Navigation.Routes
import com.example.todolist.ToDoList.ListToDoListItem
import com.example.todolist.ToDoList.ToDoListItem
import com.example.todolist.ToDoList.ToDoListItemViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.todolist.ui.theme.CompleteGreen
import com.example.todolist.ui.theme.IncompleteGrey
import kotlinx.coroutines.delay

// Home screen
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(navController: NavHostController, toDoListItemViewModel: ToDoListItemViewModel, analyticsViewModel: AnalyticsViewModel) {
    val completionPercentage by analyticsViewModel.completionPercentage.observeAsState(initial = 0)
    val completedTasks by analyticsViewModel.completedTasks.observeAsState(initial = 0)
    val incompleteTasks by analyticsViewModel.incompleteTasks.observeAsState(initial = 0)
    val tasksForTodayExist by analyticsViewModel.tasksForTodayExist.observeAsState(initial = false)

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        toDoListItemViewModel.syncDataFromFirebase()
        analyticsViewModel.fetchTaskCompletionData()

        while (true) {
            DatabaseActivity().checkValidSession(context) { isValidSession ->
                if (!isValidSession) {
                    navController.navigate(Routes.MainLogout.value)
                }
            }
            delay(5000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome to do list text
            Spacer(modifier = Modifier.size(30.dp))
            Text(
                "Welcome to To Do List!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.size(30.dp))
            Row {
                // Weather API
                WeatherScreen()

                Spacer(modifier = Modifier.width(20.dp))

                // Analytics box
                Box(
                    modifier = Modifier
                        .size(width = 185.dp, height = 185.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = { navController.navigate(Routes.Analytics.value) }),
                ) {
                    // Text for if there is completed tasks
                    val percentageText = if (tasksForTodayExist) {
                        "$completionPercentage% Complete! You got this!"
                    } else {
                        "Add some tasks to see your progress."
                    }
                    Text(
                        text = percentageText,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 10.dp),
                        color = Color.White
                    )
                    // Calculate input data for the PieChart based on task completion
                    val pieChartInput = if (tasksForTodayExist) {
                        // Tasks exist for today, show completed and incomplete tasks
                        listOf(
                            PieChartInput(
                                color = CompleteGreen,
                                value = completedTasks.toDouble(),
                                description = "Completed Tasks"
                            ),
                            PieChartInput(
                                color = IncompleteGrey,
                                value = incompleteTasks.toDouble(),
                                description = "Incomplete Tasks"
                            )
                        )
                    } else {
                        // No tasks for today, show 100% completed (purple40)
                        listOf(
                            PieChartInput(
                                color = IncompleteGrey,
                                value = 100.0, // 100% completion
                                description = "No Tasks Today"
                            )
                        )
                    }

                    // PieChart composable displaying completed and incomplete tasks as a pie chart

                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        PieChart(
                            modifier = Modifier
                                .size(120.dp)
                                .padding(bottom = 10.dp),
                            input = pieChartInput,
                            centerText = "",
                            centerLabelColor = MaterialTheme.colorScheme.primary,
                            centerTransparentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(30.dp))

            // Show full to-do list button
            Button(
                onClick = { navController.navigate(Routes.ToDoList.value) }
            ) {
                Text(text = "Show full task list")
            }

            Spacer(modifier = Modifier.size(30.dp))

            // Show today's to do list
            Text(
                text = "Today's task list",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 7.dp)
            )

            var toDoListItems by remember { mutableStateOf(emptyList<ToDoListItem>()) }
            val currentUserUid = Firebase.auth.currentUser?.uid
            toDoListItemViewModel.allToDoListItems.observeAsState(emptyList()).apply {
                toDoListItems = this.value
                    .filter { it.userId == currentUserUid || it.friend == currentUserUid } // Remove items where the user is not the user OR a friend
                    .filter { LocalDate.parse(it.dueDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")) == LocalDate.now() } // Filter by today
                    .sortedBy { it.createdAt } // Sort by creation date
            }

            if (toDoListItems.isEmpty()) {
                Text(text = "There are currently no items in today's task list!")
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyColumn {
                        itemsIndexed(toDoListItems) { _, item ->
                            ListToDoListItem(item, false, toDoListItemViewModel, navController)
                        }
                    }
                }
            }
        }
    }
}