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
import android.util.Log
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

// Main login page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLogin(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hidePassword by remember { mutableStateOf(true) }
    if (AuthenticationActivity().checkIsLoggedIn()) {
        navController.navigate(Routes.Home.value)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val mContext = LocalContext.current
    var emailError by remember { mutableStateOf(true) }
    var passwordError by remember { mutableStateOf(true) }
    var isLoginButtonClicked by remember { mutableStateOf(false) }

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

        if (emailError and isLoginButtonClicked) {
            Text("Invalid email address", color = Color.Red)
        }

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = password.isEmpty() or (password.length < 6)
                            },
            label = { Text("Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            visualTransformation = if (hidePassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                if (hidePassword) {
                    IconButton(
                        onClick = { hidePassword = false }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "hide_password"
                        )
                    }
                } else {
                    IconButton(onClick = { hidePassword = true }) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "show_password"
                        )
                    }
                }
            }
        )

        if (passwordError and isLoginButtonClicked) {
            Text("Password cannot be less than 6 characters", color = Color.Red)
        }

        // Buttons
        Row(modifier = Modifier.padding(0.dp)) {
            // Login Button
            Button(
                onClick = {
                    isLoginButtonClicked = true
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
                        AuthenticationActivity().signInWithGoogle(context, scope) { isSuccess ->
                            if (isSuccess) {
                                navController.navigate(Routes.Home.value)
                            } else {
                                Toast.makeText(
                                    mContext,
                                    "Sign-in with google failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }
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