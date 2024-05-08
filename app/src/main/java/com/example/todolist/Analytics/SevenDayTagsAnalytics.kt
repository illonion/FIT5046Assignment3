package com.example.todolist.Analytics

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.todolist.ui.theme.CompleteGreen
import com.example.todolist.ui.theme.IncompleteGrey
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.ui.theme.Purple40
import androidx.compose.ui.text.TextStyle
import com.example.todolist.DatabaseActivity
import com.example.todolist.Navigation.Routes
import com.example.todolist.ui.theme.Purple80
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevenDayTagsAnalytics(navController: NavHostController) {
    // Initialise AnalyticsViewModel
    val viewModel: AnalyticsViewModel = viewModel()

    // context
    val context = LocalContext.current

    // Observe LiveData values for completed and incomplete tasks from the ViewModel
    val completionPercentage by viewModel.completionPercentage.observeAsState(initial = 0)
    val completedTasks by viewModel.completedTasks.observeAsState(initial = 0)
    val incompleteTasks by viewModel.incompleteTasks.observeAsState(initial = 0)
    val tasksForTodayExist by viewModel.tasksForTodayExist.observeAsState(initial = false)
    val yesterdayCompletionPercentage by viewModel.yesterdayCompletionPercentage.observeAsState(initial = 0)

    // Fetch task completion data from Firebase when ViewModel is first created/updated
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchTaskCompletionData()
    }

    // Check if user logged in another device every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            DatabaseActivity().checkValidSession { isValidSession ->
                if (!isValidSession) {
                    Toast.makeText(
                        context,
                        "New log in detected on another device. please login again",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(Routes.MainLogout.value)
                }
            }
            delay(5000)
        }
    }

    // Define legend items
    val legendItems = listOf(
        LegendItem(color = CompleteGreen, label = "Completed"),
        LegendItem(color = IncompleteGrey, label = "Incomplete")
    )

    // Scaffold with TopAppBar and main content area
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "To Do List Analytics") },
                // Back button to navigate back to previous analytics screen
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("Analytics") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        // Main content inside a Box with vertical scrolling capability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 45.dp)
                .verticalScroll(rememberScrollState())
                .wrapContentWidth(Alignment.CenterHorizontally) // Center content horizontally
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display completion percentage text or message if no tasks for today
                if (tasksForTodayExist) {
                    Text(
                        text = "Today's Progress.\nYou got this!",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        color = Purple40,
                    )
                } else {
                    Text(
                        text = "Add some tasks to see your progress.",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        color = Purple40,
                    )
                }

                // Calculate input data for the PieChart based on task completion
                val pieChartInput = if (tasksForTodayExist) {
                    // Tasks exist for today, show completed and incomplete tasks
                    listOf(
                        PieChartInput(
                            color = CompleteGreen,
                            value = completedTasks?.toDouble() ?: 0.0,
                            description = "Completed Tasks"
                        ),
                        PieChartInput(
                            color = IncompleteGrey,
                            value = incompleteTasks?.toDouble() ?: 0.0,
                            description = "Incomplete Tasks"
                        )
                    )
                } else {
                    // No tasks for today, show 100% completed (purple40)
                    listOf(
                        PieChartInput(
                            color = Purple40,
                            value = 100.0, // 100% completion
                            description = "No Tasks Today"
                        )
                    )
                }

                // PieChart composable displaying completed and incomplete tasks as a pie chart
                PieChart(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.CenterHorizontally), // Align pie chart center horizontally
                    input = pieChartInput,
                    centerText = "$completionPercentage%",
                    centerLabelColor = Color.White,
                    centerTransparentColor = Color.White.copy(alpha = 0.2f)
                )

                // Display legend for pie chart
                PieChartLegend(legendItems = legendItems)

                // Display yesterday's completion percentage
                if (yesterdayCompletionPercentage > 0) {
                    Text(
                        text = "Yesterday: $yesterdayCompletionPercentage% completed.",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    AnalyticsButton(navController)
                }
            }
        }
    }
}


@Composable
fun AnalyticsButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("Analytics") },
        modifier = Modifier
            .fillMaxWidth() // Button takes full width of its parent
            .padding(16.dp), // Add padding around the button
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Purple80, // Set background color of the button
        )
    ) {
        Text(
            text = "View Today's Progress Analytics",
            style = TextStyle(fontSize = 16.sp), // Set font size of the text
            color = Purple40 // Set text color of the button text
        )
    }
}