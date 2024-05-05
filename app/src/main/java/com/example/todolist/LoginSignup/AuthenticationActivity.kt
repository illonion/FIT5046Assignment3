package com.example.todolist.LoginSignup

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.todolist.DatabaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

class AuthenticationActivity : Activity() {

    // [START declare_auth]
    private var auth: FirebaseAuth = Firebase.auth
    // [END declare_auth]

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // Initialize Firebase Auth
//        auth = Firebase.auth
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    // [END on_start_check_user]

    public fun createAccount(email: String, password: String, firstName: String, lastName: String, isSuccess:  (Boolean) -> Unit) {
        // [START create_user_with_email]
//        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    DatabaseActivity().addNewUser(user, email, firstName, lastName) { isSuccess ->
                        if (isSuccess) {
                            isSuccess(true)
                        } else {
                            isSuccess(false)
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    isSuccess(false)
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    public fun signIn(email: String, password: String, isSuccess:  (Boolean) -> Unit) {
        // [START sign_in_with_email]
//        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    isSuccess(true)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    isSuccess(false)
                }
            }
    }

    public fun signOut() {
        auth.signOut()
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {
//        unused for now
    }

    private fun reload() {
//        may be used later
    }

    companion object {
        private const val TAG = "Authentication"
    }
}