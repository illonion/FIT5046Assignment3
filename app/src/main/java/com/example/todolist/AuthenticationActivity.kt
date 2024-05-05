package com.google.firebase.quickstart.auth.kotlin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.todolist.DatabaseActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.auth
import kotlinx.coroutines.runBlocking
import java.util.UUID
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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
//            reload()
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
                    val user = auth.currentUser!!
                    val token = auth.toString()
                    DatabaseActivity().addNewUser(user, email, firstName, lastName) { isSuccess ->
                        if (isSuccess) {
                            getTokenCallback() { token ->
                                DatabaseActivity().setCurrentSessionId(user, token)
                                isSuccess(true)
                            }
                        } else {
                            user.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        isSuccess(false)
                                    }
                                }
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    isSuccess(false)
                }
            }
        // [END create_user_with_email]
    }

    public fun signIn(email: String, password: String, isSuccess:  (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    getTokenCallback() { token ->
                        DatabaseActivity().setCurrentSessionId(user, token)
                        isSuccess(true)
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    isSuccess(false)
                }
            }
    }
    public fun signOut() {
        auth.signOut()
    }
    public fun signOutCheckToken(isSuccess: (Boolean) -> Unit) {
//        unused, for test purpose only
        getTokenCallback {token ->
            println("4321@@@@@@@@@@@###@@@@@@")
            println(token)
            println(token)
            println("4321@@@@@@@@@@@###@@@@@@")
            auth.signOut()
            isSuccess(true)
        }

    }

    public fun checkIsLoggedIn(): Boolean {
        val user = Firebase.auth.currentUser
        if (user != null) {
            return true
        } else {
            return false
        }
    }

    suspend fun getTokenSuspend(): String? {
//    Use coroutine suspend to return token without using callback
//        doesn't work (IDK WHY)
        return suspendCoroutine { continuation ->
            auth.getAccessToken(false).addOnCompleteListener { task ->
                val result: GetTokenResult? = task.result
                val token: String? = result?.getToken()
                continuation.resume(token)
            }
        }
    }

    public fun getTokenCallback(token: (String?) -> Unit) {
        try {
            auth.getAccessToken(false).addOnCompleteListener { task ->
                val result: GetTokenResult? = task.result
                token(result?.getToken())
            }
        } catch(e: Exception) {
            token(null)
        }
    }

    fun getTokenAndWait(): String? {
//        does not work (seems like thread get blocked)

        val latch = CountDownLatch(1)
        var token: String? = null

        auth.getAccessToken(false).addOnCompleteListener { task ->
            val result: GetTokenResult? = task.result
            token = result?.getToken()
            latch.countDown() // Notify that the callback has finished
        }

        latch.await() // Block until the latch counts down to 0
        return token
    }
    public fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    companion object {
        private const val TAG = "Authentication"
    }
}