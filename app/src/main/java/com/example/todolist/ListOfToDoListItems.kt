package com.example.todolist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.ui.theme.Purple40
import com.example.todolist.ui.theme.Purple80
import com.example.todolist.ui.theme.lightGreen

// List of to do list items
@Composable
fun ListToDoListItem(toDoListItem: ToDoListItem, showIcon: Boolean, viewModel: ToDoListItemViewModel) {
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
                Text(
                    text = toDoListItem.name,
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 36.sp
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
                if (showIcon) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (!toDoListItem.completed) {
                            ToDoListItemIcon(Icons.Default.Check) { viewModel.markItemAsCompleted(toDoListItem.taskId) }
                        }
                        ToDoListItemIcon(Icons.Default.Edit) { }
                        ToDoListItemIcon(Icons.Default.Delete) { viewModel.deleteToDoListItem(toDoListItem) }
                    }
                }
            }
        }
    }
}

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