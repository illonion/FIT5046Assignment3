package com.example.todolist.ToDoList

import android.app.Application
import kotlinx.coroutines.flow.Flow
class ToDoListItemRepository (application: Application) {
    private var toDoListItemDao: ToDoListItemDAO = ToDoListItemDatabase.getDatabase(application).toDoListItemDAO()
    val allToDoListItems: Flow<List<ToDoListItem>> = toDoListItemDao.getAllToDoListItems()
    suspend fun insert(toDoListItem: ToDoListItem) {
        toDoListItemDao.insertToDoListItem(toDoListItem)
    }
    suspend fun delete(toDoListItem: ToDoListItem) {
        toDoListItemDao.deleteToDoListItem(toDoListItem)
    }
    suspend fun update(toDoListItem: ToDoListItem) {
        toDoListItemDao.updateToDoListItem(toDoListItem)
    }
    suspend fun clearAllToDoListItems() {
        toDoListItemDao.deleteAllToDoListItems()
    }
    suspend fun insertAllToDoListItems(toDoListItems: List<ToDoListItem>) {
        toDoListItemDao.insertToDoListItems(toDoListItems)
    }
}