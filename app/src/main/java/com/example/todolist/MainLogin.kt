package com.example.todolist

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.quickstart.auth.kotlin.AuthenticationActivity

import android.util.Log

// Main login page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLogin(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    // AuthenticationActivity().signOut()

    // Top bar
    TopAppBar(
        title = { Text(text = "Login") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(65.dp))
        // Username
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Buttons
        Row(modifier = Modifier.padding(0.dp)) {
            // Login Button
            Button(
                onClick = {
//                    AuthenticationActivity().signIn(email, password) { isSuccess ->
//                        Comment the above line and uncomment the below line
//                        when you don't want to type(just press login button)
                    AuthenticationActivity().signIn("test13@test.com", "123456") { isSuccess ->
                        if (isSuccess) {
                            navController.navigate(Routes.Home.value)
                        }
                    }
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(0.3f)
            ) {
                Text("Login")
            }

            Text(text = " -OR- ",
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterVertically)
            )

            // Google Login Button
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "GoogleSignIn",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Sign in With Google")
            }

        }

        Divider(
            modifier = Modifier.padding(bottom = 16.dp),
            thickness = 2.dp,
            color = Color.Red)

        // Signup button
        Button(
            onClick = {
                navController.navigate(Routes.MainSignup.value)
            },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Sign Up Now")
        }
    }
}