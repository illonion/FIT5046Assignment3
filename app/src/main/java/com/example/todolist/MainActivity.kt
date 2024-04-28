package com.example.todolist

import BottomNavigationBar
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.activity.viewModels
import java.text.SimpleDateFormat

class MainActivity : ComponentActivity() {
    private val friendViewModel: FriendViewModel by viewModels()
    private val toDoListViewModel: ToDoListViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toDoListViewModel.addToDoListItem(ToDoListItem("Groceries","Outdoors", SimpleDateFormat("dd/MM/yyyy").parse("23/03/2024"), "Milly",false))
        toDoListViewModel.addToDoListItem(ToDoListItem("FIT5046 Assignment 1","School", SimpleDateFormat("dd/MM/yyyy").parse("28/03/2024"), "Alec", false))
        toDoListViewModel.addToDoListItem(ToDoListItem("Groceries Again","Outdoors", SimpleDateFormat("dd/MM/yyyy").parse("28/03/2024"), "Jimmy",false))
        toDoListViewModel.addToDoListItem(ToDoListItem("Running","Outdoors", SimpleDateFormat("dd/MM/yyyy").parse("28/03/2024"), "Lawrence",false))
        toDoListViewModel.addToDoListItem(ToDoListItem("Club","Outdoors", SimpleDateFormat("dd/MM/yyyy").parse("09/03/2024"), "No one",false))
        toDoListViewModel.addToDoListItem(ToDoListItem("FIT5225 Assignment 1","School", SimpleDateFormat("dd/MM/yyyy").parse("04/04/2024"), "No one",false))
        setContent {
            BottomNavigationBar(toDoListViewModel, friendViewModel)
        }
    }
}

