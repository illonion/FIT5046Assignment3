package com.example.todolist

import java.util.Date

data class ToDoListItem(val userId: String, val name: String, val tag: String, val dueDate: Date, val friend: String, var completed: Boolean)
