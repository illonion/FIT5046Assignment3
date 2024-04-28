package com.example.todolist

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

// Home screen
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(navController: NavHostController) {
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
            verticalArrangement = Arrangement.Center,
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
                Box {
                    Image(
                        painter = painterResource(R.drawable.sunny),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 185.dp, height = 185.dp)
                    )
                    Text(
                        "Sunny",
                        modifier = Modifier.align(Alignment.TopCenter)
                            .padding(top = 10.dp),
                        color = Color.White,
                        fontSize = 30.sp,
                    )
                    Text(
                        "25C",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontSize = 80.sp
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Analytics box
                Box(
                    modifier = Modifier
                        .size(width = 185.dp, height = 185.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = { navController.navigate(Routes.Analytics.value) }),
                ) {
                    Text(
                        text = "56% Complete! You got this!",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 10.dp),
                        color = Color.White
                    )
                    Image(
                        painter = painterResource(R.drawable.homepagepiechart),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .size(width = 100.dp, height = 100.dp)
                            .align(Alignment.BottomCenter)
                    )
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

            val sampleList: List<String> = listOf("Groceries", "FIT5046 Assignment 1", "Groceries Again", "Running", "Club", "FIT5225 Assignment 1")
            ItemList(sampleList)
        }
    }
}

// Item
@Composable
fun Item(name: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(16.dp))
    {
        Text(text = name)
    }
}


// Item List
@Composable
fun ItemList(list: List<String>) {
    LazyColumn {
        list.forEachIndexed { index, listItem ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            top = 10.dp,
                            bottom = 10.dp,
                            start = 20.dp,
                            end = 20.dp
                        )
                ) {
                    Item(listItem)
                }
            }
        }
    }
}

