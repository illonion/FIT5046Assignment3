package com.example.todolist

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.todolist.LoginSignup.AuthenticationActivity

class DatabaseActivity {

    // Initializes a Firebase Realtime Database instance
    private val database =
        FirebaseDatabase.getInstance("http://fit5046-assignment-3-5083c-default-rtdb." +
                "asia-southeast1.firebasedatabase.app")

    // Make sure no multiple devices log in same account at the same time
    fun checkValidSession(context: Context, isValidSession: (Boolean) -> Unit) {
        getCurrentSessionTokenCallback { dbSessionToken ->
            AuthenticationActivity().getTokenCallback {authToken ->
                if (dbSessionToken == authToken && authToken != null) {
                    isValidSession(true)
                } else {
                    Toast.makeText(context, "New login detected on another device. " +
                            "Please login again.", Toast.LENGTH_LONG).show()
                    isValidSession(false)
                }
            }
        }
    }

    // Registration
    fun addNewUser(user: FirebaseUser?, email: String, firstName: String,
                   lastName: String, isSuccess: (Boolean) -> Unit) {
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

    // Retrieve the session token of the current user
    private fun getCurrentSessionTokenCallback(sessionToken: (String?) -> Unit) {
        // This is the one that can be used (using callback)
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

    // Set the session token for a given Firebase user
    fun setCurrentSessionId(user: FirebaseUser?, sessionToken: String?) {
        user?.let {
            database.reference.child("users").child(user.uid).child("sessionToken").setValue(sessionToken)
        }
    }
}