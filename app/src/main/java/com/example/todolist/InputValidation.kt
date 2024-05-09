package com.example.todolist


class InputValidation {
    // taskName cannot be empty or more that 25 characters long
    fun isValidTaskName(taskName: String): Boolean {
        if (taskName.trim().isEmpty()) {
            return false
        }
        return taskName.length <= 25
    }
}