package com.example.todolist.LoginSignup

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.todolist.DatabaseActivity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AuthenticationActivity : Activity() {

    private var auth: FirebaseAuth = Firebase.auth

    // google auth
    private val WEB_CLIENT_ID = "594256886966-de46qrlc59o61nksda8g7ek4c4frmsfc.apps.googleusercontent.com"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {

        }
    }

    public fun createAccount(email: String, password: String, firstName: String, lastName: String, isSuccess:  (Boolean) -> Unit) {
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
    }

    public fun signIn(email: String, password: String, rememberLogin: Boolean, sharedPref: SharedPreferences, isSuccess:  (Boolean) -> Unit) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    getTokenCallback() { token ->
                        DatabaseActivity().setCurrentSessionId(user, token)
                        with (sharedPref.edit()) {
                            if (rememberLogin) {
                                putString("email", email)
                            } else {
                                putString("email", "")
                            }
                            putBoolean("rememberLogin", rememberLogin)
                            apply()
                        }
                        isSuccess(true)
                    }

                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    isSuccess(false)
                }
            }
    }
    public fun signOut() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()
        }
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

    public fun signInWithGoogle(context: Context, scope: CoroutineScope, isSuccess: (Boolean) -> Unit) {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider
                    .getCredential(googleIdToken, null)

                val accounts = AccountManager.get(context).getAccounts()
                val email = accounts[0].name

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            getTokenCallback() { token ->
                                DatabaseActivity().addNewUser(user, email, "", "") { isSuccess ->
                                    if (isSuccess) {
                                        DatabaseActivity().setCurrentSessionId(user, token)
                                        isSuccess(true)
                                    } else {
                                        isSuccess(false)
                                    }
                                    isSuccess(true)
                                }
                            }
                        } else {
                            isSuccess(false)
                        }
                    }
            } catch (e: Exception) {
                isSuccess(false)
            }
        }
    }

    public fun checkIsLoggedIn(): Boolean {
        val user = Firebase.auth.currentUser
        return user != null
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