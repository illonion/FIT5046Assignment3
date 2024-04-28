package com.example.todolist

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class DatabaseActivity {

    val auth = FirebaseAuth.getInstance()
    val database =
        FirebaseDatabase.getInstance("http://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app")

    public fun addNewUser(user: FirebaseUser?, email: String, firstName: String, lastName: String, isSuccess: (Boolean) -> Unit) {
        user?.let {
            val userMap = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email
            )
            database.reference.child("users").child(user.uid).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isSuccess(true)
                    } else {
                        isSuccess(false)
                    }
                }
        } ?: run {isSuccess(false)}
    }

}