package com.example.todolist

import com.example.todolist.Navigation.BottomNavigationBar
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.todolist.Analytics.AnalyticsViewModel
import com.example.todolist.ToDoList.ToDoListItemViewModel

class MainActivity : ComponentActivity() {
    private val toDoListViewModel: ToDoListItemViewModel by viewModels()
    private val analyticsViewModel: AnalyticsViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomNavigationBar(toDoListViewModel, analyticsViewModel)
        }
    }
}

