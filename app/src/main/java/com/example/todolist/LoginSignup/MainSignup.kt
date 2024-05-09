package com.example.todolist.LoginSignup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todolist.Navigation.Routes
																		

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSignup(navController: NavHostController) {

    var newPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    val message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var hidePassword by remember { mutableStateOf(true) }

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
                    firstName = it.trim()
                    firstNameError = it.isEmpty()
                                },
                label = { Text("First Name *") },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(end = 8.dp)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it.trim()
                    lastNameError = it.isEmpty()
                                },
                label = { Text("Last Name *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = emailAddress,
            onValueChange = {
                emailAddress = it.trim()
                emailAddressError = !isValidEmail(it)
                            },
            label = { Text("Email *")},
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
                newPassword = it.trim()
                newPasswordError = !isValidPassword(it)
                            },
            label = { Text("New Password *")},
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
                    androidx.compose.material.IconButton(
                        onClick = { hidePassword = false }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "hide_password"
                        )
                    }
                } else {
                    androidx.compose.material.IconButton(onClick = { hidePassword = true }) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "show_password"
                        )
                    }
                }
            }
        )
        if (newPasswordError) {
            Text("Password must have a minimum length of 6 characters and cannot contain spaces", color = Color.Red)
        }

        if (message.isNotEmpty()) {
            Text(text = message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        }

        Button(
            onClick = {
                // Check empty fields
                if (firstName.isNotBlank() && lastName.isNotBlank()
                    && emailAddress.isNotBlank() && newPassword.isNotBlank())
                {
                    // Validate email address and password
                    if (!emailAddressError && !newPasswordError)
                    {
                        checkEmailExists(emailAddress) { emailExists ->
                            Log.i("Email Exists", emailExists)
                            when (emailExists) {
                                "Email Does Not Exist" -> {
                                    // If email does not exist, proceed with account creation
                                    loading = true
                                    AuthenticationActivity().createAccount(emailAddress, newPassword, firstName, lastName) { isSuccess ->
                                        loading = false
                                        if (isSuccess) {
                                            navController.navigate(Routes.Home.value)
                                        }
                                    }
                                }
                                "Email Exists" -> {
                                    // If email already exists, display an error message
                                    Toast.makeText(
                                        mContext,
                                        "An account already exists with this email.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else -> {
                                    Toast.makeText(
                                        mContext,
                                        "There was an error with the database. Please try again later.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
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
                .padding(bottom = 5.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Create New Account")
            }
        }

        // Cancel button
        Button(
            onClick = { navController.navigate("MainLogin") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text("Cancel")
        }
    }
}


fun checkEmailExists(email: String, callback: (String) -> Unit) {
    val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val mDatabase = database.reference
    val usersRef = mDatabase.child("users")

    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // If there's at least one user with the provided email, it exists
            for (snapshot in dataSnapshot.children) {
                if (snapshot.child("email").value.toString() == email) {
                    return callback("Email Exists")
                }
            }
            return callback("Email Does Not Exist")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            return callback("Database Error")
        }
    })
}