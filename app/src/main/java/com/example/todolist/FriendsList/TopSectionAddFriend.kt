package com.example.todolist.FriendsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp

// Add friend section
@Composable
fun TopSectionAddFriend (onAdd: (String) -> Unit) {
    var emailValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    Spacer(modifier = Modifier.height(65.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 10.dp
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = emailValue,
                onValueChange = { emailValue = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onAdd(emailValue) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = ""
                    )
                }
                IconButton(onClick = {
                    emailValue = ""
                    keyboardController?.hide()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}