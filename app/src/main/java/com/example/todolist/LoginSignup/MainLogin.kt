package com.example.todolist.LoginSignup

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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

import com.example.todolist.Navigation.Routes

// Main login page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLogin(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current


    val mContext = LocalContext.current
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

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
            onValueChange = {
                email = it
                emailError = !isValidEmail(it)
                            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (emailError) {
            Text("Invalid email address", color = Color.Red)
        }

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = it.isEmpty()
                            },
            label = { Text("Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (passwordError) {
            Text("Password cannot be empty", color = Color.Red)
        }

        // Buttons
        Row(modifier = Modifier.padding(0.dp)) {
            // Login Button
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        AuthenticationActivity().signIn(email, password) { isSuccess ->
                            if (!emailError && !passwordError) {
                                AuthenticationActivity().signIn(email, password)
//                    AuthenticationActivity().signIn("test13@test.com", "123456")
                                { isSuccess ->
                                    if (isSuccess) {
                                        navController.navigate(Routes.Home.value)
                                    } else {
                                        Toast.makeText(
                                            mContext,
                                            "Sign-in failed. Please check your credentials.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    else {
                        Toast.makeText(
                            mContext,
                            "Please enter your email and password.",
                            Toast.LENGTH_SHORT
                        ).show()
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



fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}