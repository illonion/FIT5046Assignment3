package com.example.todolist

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.todolist.Navigation.Routes
import com.example.todolist.ToDoList.ListToDoListItem
import com.example.todolist.ToDoList.ToDoListItem
import com.example.todolist.ToDoList.ToDoListItemViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Home screen
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(navController: NavHostController, viewModel: ToDoListItemViewModel) {
    val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val mDatabase = database.reference
    val taskReference = mDatabase.child("tasks")

    LaunchedEffect(Unit) {
        viewModel.syncDataFromFirebase()
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

            var toDoListItems by remember { mutableStateOf(emptyList<ToDoListItem>()) }
            val currentUserUid = Firebase.auth.currentUser?.uid
            viewModel.allToDoListItems.observeAsState(emptyList()).apply {
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
                        itemsIndexed(toDoListItems) { index, item ->
                            ListToDoListItem(item, false, viewModel)
                        }
                    }
                }
            }
        }
    }
}