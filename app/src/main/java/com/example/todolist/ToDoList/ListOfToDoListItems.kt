package com.example.todolist.ToDoList

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.InputValidation
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.todolist.DatabaseActivity
import com.example.todolist.Navigation.Routes
import com.example.todolist.ui.theme.Purple40
import com.example.todolist.ui.theme.lightGreen

// List of to do list items
@Composable
fun ListToDoListItem(toDoListItem: ToDoListItem, showIcon: Boolean, viewModel: ToDoListItemViewModel, navController: NavHostController?) {

    var isEditDialogVisible by remember { mutableStateOf(false) }
    var editedToDoListItem by remember { mutableStateOf(toDoListItem) }

    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(3.dp),
            shape = RoundedCornerShape(60.dp),
            colors = if (toDoListItem.completed) { CardDefaults.cardColors(containerColor = lightGreen) }
            else { CardDefaults.cardColors() }
        ) {
            Row(
                modifier = Modifier.padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // To do list item text
                Text(
                    text = addEllipsis(toDoListItem.name,25),
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 36.sp
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
                // Show completion icons on todolist page
                if (showIcon) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (!toDoListItem.completed) {
                            ToDoListItemIcon(Icons.Default.Check) {
                                // Delete item
                                DatabaseActivity().checkValidSession { isValidSession ->
                                    if (isValidSession) {
                                        viewModel.markItemAsCompleted(toDoListItem.taskId)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Session Expired, please log in again",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController?.navigate(Routes.MainLogout.value)
                                    }
                                }
                            }
                        }
                        ToDoListItemIcon(Icons.Default.Edit) {
                            isEditDialogVisible = true
                            editedToDoListItem = toDoListItem
                        }
                        ToDoListItemIcon(Icons.Default.Delete) {
                            // Delete item
                            DatabaseActivity().checkValidSession { isValidSession ->
                                if (isValidSession) {
                                    viewModel.deleteToDoListItem(toDoListItem)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Session Expired, please log in again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController?.navigate(Routes.MainLogout.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Show the EditTaskDialog if isEditDialogVisible is true
    if (isEditDialogVisible) {
        EditTaskDialog(
            toDoListItem = editedToDoListItem,
            navController = navController,
            onDismiss = { isEditDialogVisible = false },
            onSave = {
                viewModel.updateToDoListItem(it)
                isEditDialogVisible = false
            }
        )
    }
}

// To Do List Item Icon
@Composable
fun ToDoListItemIcon(
    specificIcon: ImageVector,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
    ) {
        Icon(
            imageVector = specificIcon,
            contentDescription = null,
            tint = Purple40,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Edit task dialog
@Composable
fun EditTaskDialog(toDoListItem: ToDoListItem, navController: NavHostController?, onDismiss: () -> Unit, onSave: (ToDoListItem) -> Unit) {

    val context = LocalContext.current

    var editedToDoListItem by remember { mutableStateOf(toDoListItem) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task")},
        confirmButton = {
            Button(
                onClick = {
                    if (InputValidation().isValidTaskName(editedToDoListItem.name)) {
                        onSave(editedToDoListItem)
                        onDismiss()
                        // Delete item
                        DatabaseActivity().checkValidSession { isValidSession ->
                            if (isValidSession) {
                                if (InputValidation().isValidTaskName(editedToDoListItem.name)) {
                                    onSave(editedToDoListItem)
                                    onDismiss()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Session Expired, please log in again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController?.navigate(Routes.MainLogout.value)
                            }
                        }
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        },
        text = {
            Column() {
                TextField(
                    value = editedToDoListItem.name,
                    onValueChange = { editedToDoListItem = editedToDoListItem.copy(name = it.trim()) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (!InputValidation().isValidTaskName(editedToDoListItem.name)) {
                    Text(text = "Task name cannot be empty or more than 25 characters long", color = Color.Red)
                }
            }
        }
    )
}

fun addEllipsis(s:String, n: Int): String {
    if (s.length < n) {
        return s
    }
    return s.take(n-3) + "..."
}