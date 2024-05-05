package com.example.todolist

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.todolist.LoginSignup.AuthenticationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

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
    suspend public fun getCurrentSessionToken(user: FirebaseUser?): String? {
//        does not work
        user?.let {
            val snapshot =
                database.getReference("users").child(user.uid).child("sessionToken").get().await()
            val value = snapshot.getValue(String::class.java)
            return value
        }
        return null
    }

    suspend public fun getCurrentSessionToken2(): String? {
//        does not work
        val user = AuthenticationActivity().getUser()
        user?.let {
            val snapshot =
                database.getReference("users").child(user.uid).child("sessionToken").get().await()
            val value = snapshot.getValue(String::class.java)
            return value
        }
        return null
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

    fun checkSession(): Boolean {

        var currentSessionToken: String? = null
        var token: String? = null
        val job = GlobalScope.launch(Dispatchers.Main) {
            val currentSessionDeferred = async {
                getCurrentSessionToken2()
            }

            val tokenDeferred = async {
                AuthenticationActivity().getTokenSuspend()
            }

            currentSessionToken = currentSessionDeferred.await()
            token = tokenDeferred.await()
        }

        runBlocking {
            job.join()
        }

        return currentSessionToken == token
    }

    public fun checkSessionLatch(): Boolean {
//        does not work

        // Latch to wait for both callbacks to complete
        val latch = CountDownLatch(2)
        var currentSessionToken: String? = "S-E-S"
        var token: String? = "T-O-K"

        val executor = Executors.newSingleThreadExecutor()
        var result = false
        executor.submit {

            // Get the first token using the callback
            getCurrentSessionTokenCallback {
                currentSessionToken = it
                latch.countDown()
            }

            // Get the second token using the callback
            AuthenticationActivity().getTokenCallback {
                token = it
                latch.countDown()
            }

            // Wait for both operations to complete
            latch.await()
            result = currentSessionToken == token
        }
        executor.shutdown()
        // Return whether the tokens match or not
        return result
    }
    suspend public fun isSessionExpired(): Boolean {
//        does not work
        val user = AuthenticationActivity().getUser()
        return getCurrentSessionToken(user) == AuthenticationActivity().getTokenSuspend()
    }

    public fun setCurrentSessionId(user: FirebaseUser?, sessionToken: String?) {
        user?.let {
            database.reference.child("users").child(user.uid).child("sessionToken").setValue(sessionToken)
        }
    }
}