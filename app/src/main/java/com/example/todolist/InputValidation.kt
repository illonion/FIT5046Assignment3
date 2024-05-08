package com.example.todolist

class InputValidation {
    fun isValidTaskName(taskName: String): Boolean {
        if (taskName.trim().isEmpty()) {
            return false
        }
        return taskName.length <= 25
    }
}