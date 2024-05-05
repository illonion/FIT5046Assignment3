package com.example.todolist.ToDoList

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [ToDoListItem::class], version = 2, exportSchema = false)
abstract class ToDoListItemDatabase: RoomDatabase()  {
    abstract fun toDoListItemDAO(): ToDoListItemDAO
    companion object {
        @Volatile
        private var INSTANCE: ToDoListItemDatabase? = null
        fun getDatabase(context: Context): ToDoListItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoListItemDatabase::class.java,
                    "todolistitem_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}