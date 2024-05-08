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
import com.example.todolist.ui.theme.IncompleteGrey
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.DatabaseActivity
import com.example.todolist.Navigation.Routes
import com.example.todolist.ui.theme.IndoorsPink
import com.example.todolist.ui.theme.OutdoorsGreen
import com.example.todolist.ui.theme.Purple40
import com.example.todolist.ui.theme.Purple80
import com.example.todolist.ui.theme.SchoolPurple
import com.example.todolist.ui.theme.SportsOrange
import com.example.todolist.ui.theme.WorkBlue
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevenDayTagsAnalytics(navController: NavHostController) {
    // Context
    val context = LocalContext.current

    // Initialise SevenDayViewModel
    val viewModel: SevenDayViewModel = viewModel()

    // Observe LiveData value for tasks for today existence from the ViewModel
    val tasksForTodayExist by viewModel.tasksForTodayExist.observeAsState(initial = false)

    // Fetch tag distribution data from Firebase when ViewModel is first created/updated
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchTaskTagDistribution()

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

    // Define legend items
    val legendItems = listOf(
        LegendItem(color = IndoorsPink, label = "Indoors"),
        LegendItem(color = OutdoorsGreen, label = "Outdoors"),
        LegendItem(color = WorkBlue, label = "Work"),
        LegendItem(color = SchoolPurple, label = "School"),
        LegendItem(color = SportsOrange, label = "Sports"),
        LegendItem(color = IncompleteGrey, label = "None")
    )

    // Scaffold with TopAppBar and main content area
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Analytics: Category Distribution") },
                // Back button to navigate back to progress analytics
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
                // Display text based on tasks existence for today
                Text(
                    text = if (tasksForTodayExist) "Today's Task Distribution." else "Add some tasks to see distribution.",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    color = Purple40,
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Calculate input data for the PieChart based on tag distribution
                val pieChartInput = if (tasksForTodayExist) {
                    viewModel.tagDistributionPercentage.value?.map { (tag, percentage) ->
                        val color = when (tag) {
                            "Indoors" -> IndoorsPink
                            "Outdoors" -> OutdoorsGreen
                            "Work" -> WorkBlue
                            "School" -> SchoolPurple
                            "Sports" -> SportsOrange
                            else -> IncompleteGrey // Default color for unknown tag
                        }
                        PieChartInput(
                            color = color,
                            value = percentage,
                            description = tag
                        )
                    } ?: emptyList()
                } else {
                    listOf(
                        PieChartInput(
                            color = Purple40,
                            value = 100.0,
                            description = "No Tasks Today"
                        )
                    )
                }

                // PieChart displaying tag distribution as pie chart slices
                PieChart(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.CenterHorizontally),
                    input = pieChartInput,
                    centerText = "^_^",
                    centerLabelColor = Color.White,
                    centerTransparentColor = Color.White.copy(alpha = 0.2f)
                )

                // Display legend for the pie chart
                SevenPieChartLegend(legendItems = legendItems)

                Spacer(modifier = Modifier.height(14.dp))

                AnalyticsButton(navController)
            }
        }
    }
}

@Composable
fun AnalyticsButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("Analytics") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Purple80
        )
    ) {
        Text(
            text = "Back to Daily Progress Analytics",
            style = TextStyle(fontSize = 17.sp),
            color = Purple40
        )
    }
}

@Composable
fun SevenPieChartLegend(legendItems: List<LegendItem>) {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Split legendItems into two lists of up to 3 items each
        val firstRowItems = legendItems.take(3)
        val secondRowItems = legendItems.drop(3)

        // Render legend items in two rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Render items for the first row
            firstRowItems.forEachIndexed { index, item ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(item.color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }

        // Render items for the second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            secondRowItems.forEachIndexed { index, item ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(item.color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }
    }
}
