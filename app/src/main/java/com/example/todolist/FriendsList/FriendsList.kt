package com.example.todolist.FriendsList

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolist.Navigation.Routes
import com.example.todolist.User
import com.example.todolist.ui.theme.Purple40
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsList(navController: NavHostController, friendsListViewModel: FriendsListViewModel) {

    val currentUser = Firebase.auth.currentUser
    val currentUserUid = currentUser?.uid
    val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val mDatabase = database.reference
    val friendsReference = mDatabase.child("friends")
    val usersReference = mDatabase.child("users")

    var validationMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Load all current friends
    LaunchedEffect(Unit) {
        friendsListViewModel.loadAllFriends()
    }

    // Top Bar
    TopAppBar(
        title = { Text(text = "Friends List") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
    LazyColumn {
        item {
            // Add friend
            TopSectionAddFriend(
                onAdd = { email ->
                    // Step 1: Check if they put anything
                    if (email == "") {
                        validationMessage = "Please enter something!"
                    } else {
                        // Get the friend's id
                        // Step 2: Check if the email exists
                        var emailExists = false
                        var friendUid: String? = null
                        val usersRef = mDatabase.child("users")
                        val friendsRef = mDatabase.child("friends")
                        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                for (userSnapshot in dataSnapshot.children) {
                                    if (userSnapshot.child("email").value == email) {
                                        emailExists = true
                                        friendUid = userSnapshot.key
                                        break
                                    }
                                }

                                // If email does not exist
                                if (!emailExists) {
                                    validationMessage = "This email does not exist!"
                                    return
                                }

                                // If friendUid is the same as the current User
                                if (friendUid == currentUserUid) {
                                    validationMessage = "You can't add yourself as a friend!"
                                    return
                                }

                                // Step 3: Check if they are already in the friends list with each other
                                var friendAlreadyExists = false
                                val allFriendListIds = mutableListOf<String>()
                                friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (friendsSnapshot in dataSnapshot.children) {
                                            val friend1Id = friendsSnapshot.child("friendId1").value
                                            val friend2Id = friendsSnapshot.child("friendId2").value
                                            allFriendListIds.add(friendsSnapshot.key ?: "")

                                            if ((friend1Id == currentUserUid && friend2Id == friendUid) ||
                                                (friend2Id == currentUserUid && friend1Id == friendUid)) {
                                                friendAlreadyExists = true
                                                break
                                            }
                                        }

                                        if (friendAlreadyExists) {
                                            validationMessage = "You have already added this friend!"
                                            return
                                        } else {
                                            val friendList = FriendList(currentUserUid, friendUid)
                                            var validFriendListId = false

                                            // Check valid UUID
                                            var friendListId = "friendList_" + UUID.randomUUID().toString()
                                            while (!validFriendListId) {
                                                if (allFriendListIds.contains(friendListId)) {
                                                    friendListId = "friendList_" + UUID.randomUUID().toString()
                                                } else {
                                                    validFriendListId = true
                                                }
                                            }
                                            friendsRef.child(friendListId).setValue(friendList)
                                                .addOnSuccessListener {
                                                    navController.navigate(Routes.FriendsList.value)
                                                    validationMessage = ""
                                                    Toast.makeText(context, "Successfully added friend!", Toast.LENGTH_LONG).show()
                                                }
                                                .addOnFailureListener {
                                                    validationMessage = "Sorry! Something went wrong. Please try again later."
                                                }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        validationMessage = "Sorry! Something went wrong. Please try again later."
                                        return
                                    }
                                })

                            }

                            override fun onCancelled(error: DatabaseError) {
                                validationMessage = "Sorry! Something went wrong. Please try again later."
                            }
                        })
                    }
                }
            )

            if (validationMessage != "") {
                Text(
                    text = validationMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    style = TextStyle(
                        fontSize = 18.sp,
                    ),
                    color = Color.Red
                )
            }
        }
    }

    var currentFriendsListUsers = friendsListViewModel.getFriendsListUsers
    Log.i("Friend", currentFriendsListUsers.size.toString())
    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(270.dp))
        LazyColumn {
            itemsIndexed(currentFriendsListUsers) { _, item ->
                Log.i("Friend", item.firstName)
                ListFriends(item, friendsReference) {
                    currentFriendsListUsers = currentFriendsListUsers.filter { it.userId != item.userId }.toMutableList()
                }
            }
        }
    }
}