package com.example.todolist.Analytics

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.example.todolist.ui.theme.Purple40
import com.example.todolist.DatabaseActivity
import com.example.todolist.Navigation.Routes
import kotlinx.coroutines.delay

// Composable function for displaying analytics
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Analytics(navController: NavHostController, analyticsViewModel: AnalyticsViewModel) {
    // context
    val context = LocalContext.current

    // Check if user logged in another device every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            DatabaseActivity().checkValidSession(context) { isValidSession ->
                if (!isValidSession) {
                    navController.navigate(Routes.MainLogout.value)
                }
            }
            delay(5000)
        }
    }

    // Observe LiveData values for completed and incomplete tasks from the ViewModel
    val completionPercentage by analyticsViewModel.completionPercentage.observeAsState(initial = 0)
    val completedTasks by analyticsViewModel.completedTasks.observeAsState(initial = 0)
    val incompleteTasks by analyticsViewModel.incompleteTasks.observeAsState(initial = 0)
    val tasksForTodayExist by analyticsViewModel.tasksForTodayExist.observeAsState(initial = false)
    val yesterdayCompletionPercentage by analyticsViewModel.yesterdayCompletionPercentage.observeAsState(initial = 0)

    // Fetch task completion data from Firebase when ViewModel is first created/updated
    LaunchedEffect(key1 = analyticsViewModel) {
        analyticsViewModel.fetchTaskCompletionData()
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
                title = { Text(text = "Analytics: Daily Progress") },
                // Back button to navigate back to home screen
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("Home") }) {
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
    )
    {
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
            )
            {
                Spacer(modifier = Modifier.height(30.dp))
                // Display completion percentage text or message if no tasks for today
                val currentProgressText = if (tasksForTodayExist) {
                    "Today's Progress.\nYou got this!"
                } else {
                    "Add some tasks to see your progress."
                }
                Text(
                    text = currentProgressText,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    color = Purple40,
                )
                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(14.dp))

                // Display yesterday's completion percentage
                if (yesterdayCompletionPercentage > completionPercentage) {
                    val difference = yesterdayCompletionPercentage - completionPercentage
                    Text(
                        text = "Yesterday was $difference% more productive.",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }

                AnalyticsButton(navController, "SevenDayTagsAnalytics",
                    "View 7 Day Category Analytics")
            }
        }
    }
}

@Composable
fun PieChartLegend(legendItems: List<LegendItem>) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        // All legend items
        legendItems.forEachIndexed { index, item ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Draw color indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(item.color)
            )

            // Display legend label
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

// Legend Item
data class LegendItem(
    val color: Color,
    val label: String
)