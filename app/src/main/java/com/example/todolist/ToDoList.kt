package com.example.todolist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoList(navController: NavHostController, viewModel: ToDoListItemViewModel) {
    val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val mDatabase = database.reference
    val usersRef = mDatabase.child("users")

    var completeIsExpanded by remember { mutableStateOf(false) }
    val complete = listOf("Not Completed", "Completed", "All")
    var selectedComplete by remember { mutableStateOf(complete[0]) }

    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    LaunchedEffect(Unit) {
        val updatedUserList = mutableListOf<User>()
        viewModel.syncDataFromFirebase()
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            // Getting the data
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (userSnapshot in dataSnapshot.children) {
                    val userId = userSnapshot.key.toString()
                    val userFirstName = userSnapshot.child("firstName").value.toString()
                    val userLastName = userSnapshot.child("lastName").value.toString()
                    val userEmail = userSnapshot.child("email").value.toString()
                    updatedUserList.add(User(userId, userFirstName, userLastName, userEmail))
                }
                userList = updatedUserList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // Top bar
    TopAppBar(
        title = { Text(text = "Task List") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
            MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(200.dp))
        // Add new item (CreateToDoListItem.kt)
        Button(
            onClick = { navController.navigate(Routes.CreateToDoListItem.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text("Add new task")
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Filter by completeness
        Row() {
            Text(
                text = "Complete: ",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 32.sp
                ),
                modifier = Modifier.padding(
                    PaddingValues(
                        start = 4.dp,
                        top = 8.dp,
                        end = 4.dp,
                        bottom = 8.dp
                    )
                )
            )
            ExposedDropdownMenuBox(
                expanded = completeIsExpanded,
                onExpandedChange = { completeIsExpanded = it },
                modifier = Modifier
                    .width(135.dp)
                    .height(30.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(30.dp))
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(30.dp))
            ) {
                Box(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .focusProperties { canFocus = false },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(selectedComplete)
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = completeIsExpanded)
                    }
                }
                ExposedDropdownMenu(
                    expanded = completeIsExpanded,
                    onDismissRequest = { completeIsExpanded = false }
                ) {
                    complete.forEach {
                        selectedOption -> DropdownMenuItem(
                            text = { Text(selectedOption) },
                            onClick = {
                                selectedComplete = selectedOption
                                completeIsExpanded = false
                            },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }

        // Column of to do list items
        val toDoListItems by viewModel.allToDoListItems.observeAsState(emptyList())
        Column () {
            LazyColumn {
                itemsIndexed(toDoListItems) { index, item ->
                    ListToDoListItem(item, userList)
                    Divider(color = Color.Gray, thickness = 5.dp)
                }
            }
        }
    }
}

// List of to do list items
@Composable
fun ListToDoListItem(toDoListItem: ToDoListItem, userList: List<User>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Item: ${toDoListItem.name}")
            Text(text = "Tag: ${toDoListItem.tag}")
            Text(text = "Due Date: ${toDoListItem.dueDate}")
            for (user in userList) {
                if (user.userId == toDoListItem.friend) {
                    Text(text = "Friend: ${user.firstName} ${user.lastName}")
                    break
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}