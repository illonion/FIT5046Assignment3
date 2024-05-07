/*package com.example.todolist.Analytics

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.ui.theme.Purple40

// Check if user is authenticated
val auth: FirebaseAuth = FirebaseAuth.getInstance()
val currentUser: FirebaseUser? = auth.currentUser

// Get user ID
val userId: String = currentUser?.uid ?: ""

// Composable function for displaying analytics
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Analytics(navController: NavHostController) {
    // Initialise AnalyticsViewModel
    val viewModel: AnalyticsViewModel = viewModel()

    // Observe LiveData values for completed and incomplete tasks from the ViewModel
    val completionPercentage by viewModel.completionPercentage.observeAsState(initial = 0)
    val completedTasks by viewModel.completedTasks.observeAsState(initial = 0)
    val incompleteTasks by viewModel.incompleteTasks.observeAsState(initial = 0)

    // Fetch task completion data from Firebase when ViewModel is first created/updated
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchTaskCompletionData()
    }

    //Define legend items
    val legendItems = listOf(
        LegendItem(color = CompleteGreen, label = "Completed"),
        LegendItem(color = IncompleteGrey, label = "Incomplete")
    )

    // Scaffold with TopAppBar and main content area
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "To Do List Analytics") },
                // Back button to navigate back to home screen
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { navController.navigate("Home") }) {
                        androidx.compose.material3.Icon(
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
                modifier = Modifier.align(Alignment.Center)
            ) {
                // Text displaying task completion percentage for today
                Spacer(modifier = Modifier.height(70.dp))
                Text(
                    text = "$completionPercentage% completed today.\nYou got this!",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    color = Purple40,
                )

                // PieChart composable displaying completed and incomplete tasks as a pie chart
                PieChart(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.CenterHorizontally), // Align pie chart center horizontally
                    input = listOf(
                        PieChartInput(
                            color = CompleteGreen,
                            value = completedTasks?.toDouble() ?: 0.0, // Handle nullability
                            description = "Completed Tasks"
                        ),
                        PieChartInput(
                            color = IncompleteGrey,
                            value = incompleteTasks?.toDouble() ?: 0.0, // Handle nullability
                            description = "Incomplete Tasks"
                        )
                    ),
                    centerText = "Your Progress"
                )


                //Display legend for pie chart
                PieChartLegend(legendItems = legendItems)

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
            .wrapContentHeight(), // Adjust height to wrap content
        horizontalArrangement = Arrangement.Center, // Center items horizontally
        verticalAlignment = Alignment.Top
    ) {
        legendItems.forEachIndexed { index, item ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between legend items
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

data class Task(
    val name: String,
    val completed: Boolean,
    val completedAt: Long //Timestamp when task was completed
)

data class LegendItem(
    val color: Color,
    val label: String
)
 */

package com.example.todolist.Analytics

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.ui.theme.Purple40

// Composable function for displaying analytics
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Analytics(navController: NavHostController) {
    // Initialise AnalyticsViewModel
    val viewModel: AnalyticsViewModel = viewModel()

    // Observe LiveData values for completed and incomplete tasks from the ViewModel
    val completionPercentage by viewModel.completionPercentage.observeAsState(initial = 0)
    val completedTasks by viewModel.completedTasks.observeAsState(initial = 0)
    val incompleteTasks by viewModel.incompleteTasks.observeAsState(initial = 0)
    val tasksForTodayExist by viewModel.tasksForTodayExist.observeAsState(initial = false)

    // Fetch task completion data from Firebase when ViewModel is first created/updated
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchTaskCompletionData()
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
                modifier = Modifier.align(Alignment.Center)
            ) {
                // Display completion percentage text or message if no tasks for today
                if (tasksForTodayExist) {
                    Text(
                        text = "$completionPercentage% completed today.\nYou got this!",
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
                    centerText = "Your Progress"
                )

                // Display legend for pie chart
                PieChartLegend(legendItems = legendItems)
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
            .wrapContentHeight(), // Adjust height to wrap content
        horizontalArrangement = Arrangement.Center, // Center items horizontally
        verticalAlignment = Alignment.Top
    ) {
        legendItems.forEachIndexed { index, item ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between legend items
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

data class Task(
    val name: String,
    val completed: Boolean,
    val completedAt: Long //Timestamp when task was completed
)

data class LegendItem(
    val color: Color,
    val label: String
)

