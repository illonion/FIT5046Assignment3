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
import com.example.todolist.R
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


class AuthenticationActivity : Activity() {

    private var auth: FirebaseAuth = Firebase.auth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public override fun onStart() {
        super.onStart()
    }
    fun createAccount(email: String, password: String, firstName: String, lastName: String, isSuccess:  (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser!!
                    DatabaseActivity().addNewUser(user, email, firstName, lastName) { isSuccess ->
                        if (isSuccess) {
                            getTokenCallback { token ->
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

    fun signIn(email: String, password: String, rememberLogin: Boolean, sharedPref: SharedPreferences, isSuccess:  (Boolean) -> Unit) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    getTokenCallback { token ->
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
    fun signOut() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()
        }
    }
    fun signInWithGoogle(context: Context, scope: CoroutineScope, rememberLogin: Boolean, sharedPref: SharedPreferences, isSuccess: (Boolean) -> Unit) {

        // google auth
        val WEB_CLIENT_ID = context.getString(R.string.WEB_CLIENT_ID)

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

                val accounts = AccountManager.get(context).accounts
                val email = accounts[0].name

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            getTokenCallback { token ->
                                DatabaseActivity().addNewUser(user, email, "", "") { isSuccess ->
                                    if (isSuccess) {
                                        DatabaseActivity().setCurrentSessionId(user, token)
                                        with (sharedPref.edit()) {
                                            putString("email", "")
                                            putBoolean("rememberLogin", rememberLogin)
                                            apply()
                                        }
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

    fun checkIsLoggedIn(): Boolean {
        val user = Firebase.auth.currentUser
        return user != null
    }

    fun getTokenCallback(token: (String?) -> Unit) {
        try {
            auth.getAccessToken(false).addOnCompleteListener { task ->
                val result: GetTokenResult? = task.result
                token(result?.token)
            }
        } catch(e: Exception) {
            token(null)
        }
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    companion object {
        private const val TAG = "Authentication"
    }
}