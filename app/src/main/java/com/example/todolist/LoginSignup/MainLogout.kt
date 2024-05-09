package com.example.todolist.LoginSignup

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.todolist.Navigation.Routes

// Main logout page(For signout purpose before going to main login page)
@Composable
fun MainLogout(navController: NavHostController) {
    AuthenticationActivity().signOut()
    navController.navigate(Routes.MainLogin.value)
}