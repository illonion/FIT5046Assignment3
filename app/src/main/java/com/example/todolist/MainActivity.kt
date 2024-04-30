package com.example.todolist

import BottomNavigationBar
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi

class MainActivity : ComponentActivity() {
    private val friendViewModel: FriendViewModel by viewModels()
    private val toDoListViewModel: ToDoListItemViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomNavigationBar(toDoListViewModel, friendViewModel)
        }
    }
}

