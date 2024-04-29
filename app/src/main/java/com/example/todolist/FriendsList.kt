package com.example.todolist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsList(friendViewModel: FriendViewModel, navController: NavHostController) {
    val friends = friendViewModel.friends

    val currentUser = Firebase.auth.currentUser
    val currentUserUid = currentUser?.uid
    val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val mDatabase = database.reference

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
                    friendViewModel.addFriend(Users(email))

                    // Step 1: Check if they put anything
                    // TODO("Not yet implemented")

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
                            // TODO("Not yet implemented")
                            if (!emailExists) {
                                return
                            }

                            // Step 3: Check if they are already in the friends list with each other
                            var friendAlreadyExists = false
                            var allFriendListIds = mutableListOf<String>()
                            friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val friend1Id = userSnapshot.child("friendId1").value
                                        val friend2Id = userSnapshot.child("friendId2").value
                                        allFriendListIds.add(userSnapshot.key ?: "")

                                        if (currentUserUid != null) {
                                            Log.i("ID", currentUserUid)
                                            Log.i("ID", friend1Id.toString())
                                        }
                                        if (friendUid != null) {
                                            Log.i("ID", friendUid!!)
                                            Log.i("ID", friend2Id.toString())
                                        }
                                        if ((friend1Id == currentUserUid && friend2Id == friendUid) ||
                                            (friend2Id == currentUserUid && friend1Id == friendUid)) {
                                            friendAlreadyExists = true
                                            break
                                        }
                                    }

                                    if (friendAlreadyExists) {
                                        TODO("Not yet implemented")
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
                                            }
                                            .addOnFailureListener { e -> } // Probably make a toast or something
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            )
        }
        // For each list of friends
        itemsIndexed(friends.value) { index, username ->
            ListFriends(friend = username, onDelete = {
                friendViewModel.deleteFriend(index) })
            Divider(color = Color.Gray, thickness = 5.dp)
        }
    }
}

// List of friends
@Composable
fun ListFriends(friend: Users, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onDelete() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "username: ${friend.username}")
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}

// Add friend section
@Composable
fun TopSectionAddFriend (onAdd: (String) -> Unit) {
    var emailValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    Spacer(modifier = Modifier.height(65.dp))
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 10.dp
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = emailValue,
                onValueChange = { emailValue = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onAdd(emailValue) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = ""
                    )
                }
                IconButton(onClick = {
                    emailValue = ""
                    keyboardController?.hide()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}