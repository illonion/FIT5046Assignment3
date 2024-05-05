package com.example.todolist

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.remember
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
    val completedTasks = viewModel.completedTasks
    val incompleteTasks = viewModel.incompleteTasks
    val completionPercentage = viewModel.completionPercentage

    // Fetch task completion data from Firebase when ViewModel is first created/updated
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchTaskCompletionData()
    }

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
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                // Text displaying task completion percentage for today
                Spacer(modifier = Modifier.height(70.dp))
                Text(
                    text = "${completionPercentage.value}% completed today.\nYou got this!",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    color = Purple40,
                )

                // PieChart composable displaying completed and incomplete tasks as a pie chart
                PieChart(
                    modifier = Modifier
                        .size(500.dp),
                    input = listOf(
                        PieChartInput(
                            color = CompleteGreen,
                            value = completedTasks.value?.toDouble() ?: 0.0,
                            description = "Completed Tasks"
                        ),
                        PieChartInput(
                            color = IncompleteGrey,
                            value = incompleteTasks.value?.toDouble() ?: 0.0,
                            description = "Incomplete Tasks"
                        )
                    ),
                    centerText = "Your Progress"
                )

            }
        }
    }
}

data class Task(
    val name: String,
    val completed: Boolean,
    val completedAt: Long //Timestamp when task was completed
)