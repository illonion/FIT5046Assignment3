package com.example.todolist.LoginSignup

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.todolist.Navigation.Routes

// Main logout page(For signout purpose before going to main login page)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLogout(navController: NavHostController) {
    AuthenticationActivity().signOut()
    navController.navigate(Routes.MainLogin.value)
}