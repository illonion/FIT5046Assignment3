package com.example.todolist.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Poll
import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarItem (
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    fun navBarItems(): List<NavBarItem> {
        return listOf(
            // Home
            NavBarItem(
                label = "Home",
                icon = Icons.Filled.Home,
                route = Routes.Home.value
            ),
            // Friends List
            NavBarItem(
                label = "Friends",
                icon = Icons.Filled.Person,
                route = Routes.FriendsList.value
            ),
            // To Do List
            NavBarItem(
                label = "Task List",
                icon = Icons.Filled.CheckCircle,
                route = Routes.ToDoList.value
            ),
            // Analytics
            NavBarItem(
                label = "Analytics",
                icon = Icons.Filled.Poll,
                route = Routes.Analytics.value
            ),
            // Log out
            NavBarItem(
                label = "Log Out",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                route = Routes.MainLogout.value
            ),
        )
    }
}