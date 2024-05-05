package com.example.todolist.LoginSignup

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.google.firebase.auth.FirebaseAuth
import com.example.todolist.Navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSignup(navController: NavHostController) {

    var newPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()


    // variables for validation
    val mContext = LocalContext.current
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailAddressError by remember { mutableStateOf(false) }
    var newPasswordError by remember { mutableStateOf(false) }



    // Top Bar
    TopAppBar(
        title = { Text(text = "Create Account") },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("MainLogin") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
    Column(
        modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(65.dp))

        Row(modifier = Modifier.padding(0.dp)) {
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstNameError = it.isEmpty()
                                },
                label = { Text("First Name") },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(end = 8.dp)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    lastNameError = it.isEmpty()
                                },
                label = { Text("Last Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = emailAddress,
            onValueChange = {
                emailAddress = it
                emailAddressError = !isValidEmail(it)
                            },
            label = { Text("Email Address")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        if (emailAddressError) {
            Text("Invalid email address", color = Color.Red)
        }


        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                newPasswordError = !isValidPassword(it)
                            },
            label = { Text("New Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        if (newPasswordError) {
            Text("Password must have a minimum length of 6 characters and cannot contain spaces", color = Color.Red)
        }


        if (message.isNotEmpty()) {
            Text(text = message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        }

        Button(
            onClick = {
                if (firstName.isNotBlank() && lastName.isNotBlank()
                    && emailAddress.isNotBlank() && newPassword.isNotBlank())
                {
                    if (!emailAddressError && !newPasswordError)
                    {
                        loading = true
                        AuthenticationActivity().createAccount(emailAddress, newPassword, firstName, lastName) { isSuccess ->
                            loading = false
                            if (isSuccess) {
                                navController.navigate(Routes.Home.value)
                            }
                        }
                    }

                }
                else {
                    Toast.makeText(
                        mContext,
                        "No fields could be empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Create New Account")
            }
        }
    }
}


