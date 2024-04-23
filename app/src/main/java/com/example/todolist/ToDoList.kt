package com.example.todolist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
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
import java.text.SimpleDateFormat

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoList(navController: NavHostController, viewModel: ToDoListViewModel) {
    var completeIsExpanded by remember { mutableStateOf(false) }
    val complete = listOf("Not Completed", "Completed", "All")
    var selectedComplete by remember { mutableStateOf(complete[0]) }

    // Top bar
    TopAppBar(
        title = { Text(text = "To Do List") },
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
            Text("Add new item")
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
    }
    // Column of to do list items
    val toDoListItems = viewModel.toDoListItems
    Column () {
        Spacer(modifier = Modifier.height(310.dp))
        LazyColumn {
            itemsIndexed(toDoListItems.value) { index, toDoListItem ->
                ListToDoListItem(toDoListItem = toDoListItem, onDelete = {
                    viewModel.deleteToDoListItem(index) })
                Divider(color = Color.Gray, thickness = 5.dp)
            }
        }
    }
}

// List of to do list items
@Composable
fun ListToDoListItem(toDoListItem: ToDoListItem, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onDelete() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Item: ${toDoListItem.item}")
            Text(text = "Tag: ${toDoListItem.tag}")
            Text(text = "Due Date: ${SimpleDateFormat("dd/MM/yyyy").format(toDoListItem.dueDate)}")
            Text(text = "Friend: ${toDoListItem.friend}")
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