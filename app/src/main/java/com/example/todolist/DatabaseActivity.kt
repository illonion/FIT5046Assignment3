package com.example.todolist

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.todolist.LoginSignup.AuthenticationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class DatabaseActivity() {

    private val database =
        FirebaseDatabase.getInstance("http://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app")

    public fun checkValidSession(context: Context, isValidSession: (Boolean) -> Unit) {
        getCurrentSessionTokenCallback() { dbSessionToken ->
            AuthenticationActivity().getTokenCallback() {authToken ->
                if (dbSessionToken == authToken && authToken != null) {
                    isValidSession(true)
                } else {
                    Toast.makeText(context, "New login detected on another device. Please login again.", Toast.LENGTH_LONG).show()
                    isValidSession(false)
                }
            }
        }
    }

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
    public fun getCurrentSessionTokenCallback(sessionToken: (String?) -> Unit) {
//        This is the one that can be used (using callback)
        val user = AuthenticationActivity().getUser()
        user?.let {
            val ref = database.getReference("users").child(user.uid).child("sessionToken")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if the sessionToken node exists
                    if (dataSnapshot.exists()) {
                        // Retrieve and print the sessionToken value
                        val token = dataSnapshot.getValue(String::class.java)
                        sessionToken(token)
                    } else {
                        sessionToken(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occurred
                    println("Database error: ${databaseError.message}")
                    sessionToken(null)
                }
            })
        }
    }

    public fun setCurrentSessionId(user: FirebaseUser?, sessionToken: String?) {
        user?.let {
            database.reference.child("users").child(user.uid).child("sessionToken").setValue(sessionToken)
        }
    }
}