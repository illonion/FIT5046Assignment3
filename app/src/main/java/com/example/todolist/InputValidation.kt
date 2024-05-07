package com.example.todolist

class InputValidation {
//
    public fun isValidTaskName(taskName: String): Boolean {
        if (taskName.isEmpty()) {
            return false
        }
        if (taskName.length > 25) {
            return false
        }
        return true
    }
//
//    public fun isNotEmpty(value): Boolean {
//        if (value == null) {
//            return false
//        }
//        if (value == "") {
//            return false
//        }
//        return true
//    }
}