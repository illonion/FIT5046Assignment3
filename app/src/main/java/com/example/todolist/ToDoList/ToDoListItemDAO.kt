package com.example.todolist.ToDoList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoListItemDAO {
    @Query("SELECT * FROM ToDoListItem")
    fun getAllToDoListItems(): Flow<List<ToDoListItem>>
    @Insert
    suspend fun insertToDoListItem(toDoListItem: ToDoListItem)
    @Update
    suspend fun updateToDoListItem(toDoListItem: ToDoListItem)
    @Delete
    suspend fun deleteToDoListItem(toDoListItem: ToDoListItem)
    @Query("DELETE FROM ToDoListItem")
    suspend fun deleteAllToDoListItems()
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoListItems(toDoListItems: List<ToDoListItem>)
}