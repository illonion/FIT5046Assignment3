package com.example.todolist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ToDoListViewModel: ViewModel() {
    val toDoListItems: MutableState<List<ToDoListItem>> =
        mutableStateOf(emptyList())
    fun addToDoListItem(todoListItem: ToDoListItem) {
        toDoListItems.value= toDoListItems.value.toMutableList().apply()
        { add(todoListItem) }

    }
    fun deleteToDoListItem(index: Int) {
        if (index >= 0) {
            toDoListItems.value = toDoListItems.value.toMutableList().apply()
            { removeAt(index) }
        }
    }
}
