package com.example.todolist.Navigation

import CreateToDoListItem
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todolist.LoginSignup.MainSignup
import com.example.todolist.Analytics.Analytics
import com.example.todolist.Analytics.AnalyticsViewModel
import com.example.todolist.Analytics.SevenDayTagsAnalytics
import com.example.todolist.Analytics.SevenDayViewModel
import com.example.todolist.FriendsList.FriendsList
import com.example.todolist.FriendsList.FriendsListViewModel
import com.example.todolist.Home
import com.example.todolist.LoginSignup.AuthenticationActivity
import com.example.todolist.LoginSignup.MainLogin
import com.example.todolist.LoginSignup.MainLogout
import com.example.todolist.ToDoList.ToDoList
import com.example.todolist.ToDoList.ToDoListItemViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
// Bottom navigation and the navigation controller
fun BottomNavigationBar(toDoListViewModel: ToDoListItemViewModel, analyticsViewModel: AnalyticsViewModel,
                        friendsListViewModel: FriendsListViewModel, sevenDaysViewModel: SevenDayViewModel) {

    // Check remember login
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("rememberLoginRef", Context.MODE_PRIVATE)
    val rememberLogin = sharedPref.getBoolean("rememberLogin", false)
    if (!rememberLogin) {
        AuthenticationActivity().signOut()
    }

    // Navigation controller
    val navController = rememberNavController()
    Scaffold(
        // Bottom bar
        bottomBar = {
            val navBackStackEntry by
            navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            if (currentDestination?.route !in listOf(Routes.MainLogin.value, Routes.MainSignup.value)) {
                BottomNavigation (backgroundColor= Color.LightGray ){
                    NavBarItem().navBarItems().forEach { navItem ->
                        BottomNavigationItem(
                            icon = { Icon(navItem.icon, contentDescription = null) },
                            label = { Text(
                                text = navItem.label,
                                fontSize = 13.sp
                            ) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == navItem.route
                            } == true,
                            onClick = {
                                navController.navigate(navItem.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ){ paddingValues ->
        // All nav hosts
        NavHost(
            navController,
            startDestination = Routes.MainLogin.value,
            Modifier.padding(paddingValues)
        ) {
            composable(Routes.Analytics.value) {
                Analytics(navController, analyticsViewModel)
            }
            composable(Routes.CreateToDoListItem.value) {
                CreateToDoListItem(navController, toDoListViewModel)
            }
            composable(Routes.Home.value) {
                Home(navController, toDoListViewModel, analyticsViewModel)
            }
            composable(Routes.MainLogin.value) {
                MainLogin(navController)
            }
            composable(Routes.MainLogout.value) {
                MainLogout(navController)
            }
            composable(Routes.MainSignup.value) {
                MainSignup(navController)
            }
            composable(Routes.FriendsList.value) {
                FriendsList(navController, friendsListViewModel)
            }
            composable(Routes.SevenDayTagsAnalytics.value) {
                SevenDayTagsAnalytics(navController, sevenDaysViewModel)
            }
            composable(Routes.ToDoList.value) {
                ToDoList(navController, toDoListViewModel)
            }
        }
    }
}