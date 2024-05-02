package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ToDoListItem(
    @PrimaryKey val taskId: String,
    val userId: String,
    val name: String,
    val tag: String,
    val dueDate: String,
    val friend: String,
    var completed: Boolean,
    val createdAt: Long
)