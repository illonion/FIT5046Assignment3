package com.example.todolist

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Analytics(navController: NavHostController) {
    // Top Bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "To Do List Analytics") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                // Text for task completion for today
                Spacer(modifier = Modifier.height(70.dp))
                Text(
                    text = "You have currently completed 56% of your tasks for today!\nYou got this!",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )
                // Task Completion Pie Chart
                Image(
                    painter = painterResource(R.drawable.today_completed),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 301.dp, height = 353.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Task completion for yesterday (only if there are tasks yesterday)
                Text(
                    text = "Compared to yesterday, you are currently down 2%!\nAlmost there!",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Texts for last 7 days for tags
                Text(
                    text = "Over the last 7 days, here's what your tasks look like!",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )
                // Tag last 7 days Pie Chart
                Image(
                    painter = painterResource(R.drawable.tags),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 592.dp, height = 486.dp)
                )
            }
        }
    }
}

// Silly comment