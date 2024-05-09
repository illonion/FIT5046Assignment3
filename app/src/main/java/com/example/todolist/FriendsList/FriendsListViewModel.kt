package com.example.todolist.FriendsList

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.todolist.LoginSignup.AuthenticationActivity
import com.example.todolist.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class FriendsListViewModel(application: Application): AndroidViewModel(application) {
    // Database
    private val currentUserUid = AuthenticationActivity().getUser()?.uid
    private val databaseReference = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val friendsReference = databaseReference.child("friends")
    private val usersReference = databaseReference.child("users")

    // Validation message
    private var _validationMessage = ""

    // Friends List
    private var friendListUserIds = mutableListOf<String>()
    private var friendListUsers = mutableListOf<User>()

    // Get validation message
    val getValidationMessage: String
        get() = _validationMessage

    // Get friends list
    val getFriendsListUsers: MutableList<User>
        get() = friendListUsers

    // Load all friends
    fun loadAllFriends() {
        friendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(friendsSnapshot: DataSnapshot) {
                val newFriendListUserIds = mutableListOf<String>() // Temporary list to hold updated friends ids
                val newFriendListUsers = mutableListOf<User>() // Temporary list to hold updated friends

                // Check if the user is in the friend object, and add the friend to the list of ids
                for (snapshot in friendsSnapshot.children) {
                    val friendId1 = snapshot.child("friendId1").value.toString()
                    val friendId2 = snapshot.child("friendId2").value.toString()

                    if (friendId1 == currentUserUid) { newFriendListUserIds.add(friendId2) }
                    else if (friendId2 == currentUserUid) { newFriendListUserIds.add(friendId1) }
                }

                // Replace old list of friend ids with new list
                friendListUserIds = newFriendListUserIds

                usersReference.addListenerForSingleValueEvent(object: ValueEventListener {
                    // If found, add the friend's details into the friends list
                    override fun onDataChange(usersSnapshot: DataSnapshot) {
                        if (usersSnapshot.exists()) {
                            for (snapshot in usersSnapshot.children) {
                                val userId = snapshot.key
                                if (userId != null && friendListUserIds.contains(userId)) {
                                    newFriendListUsers.add(
                                        User(
                                            userId,
                                            snapshot.child("firstName").value.toString(),
                                            snapshot.child("lastName").value.toString(),
                                            snapshot.child("email").value.toString()
                                        )
                                    )
                                }
                            }

                            // Update friendListUsers with the new list of friends
                            friendListUsers = newFriendListUsers
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _validationMessage = "Something went wrong with loading your friends! " +
                                "Please try again later."
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                _validationMessage = "Something went wrong with loading your friends! " +
                        "Please try again later."
            }
        })
    }

    // Add friend to friends list
    fun addToFriendsList(email: String) {
        // Step 1: Check if they put anything
        if (email == "") {
            _validationMessage = "Please enter something!"
            return
        }

        // Get the friend's id
        // Step 2: Check if the email exists
        var emailExists = false
        var friendUid: String? = null
        val usersRef = databaseReference.child("users")
        val friendsRef = databaseReference.child("friends")
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
                    _validationMessage = "This email does not exist!"
                    return
                }

                // If friendUid is the same as the current User
                if (friendUid == currentUserUid) {
                    _validationMessage = "You can't add yourself as a friend!"
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

                        // If the friend already exists
                        if (friendAlreadyExists) {
                            _validationMessage = "You have already added this friend!"
                            return
                        }

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

                        // Add friend
                        friendsRef.child(friendListId).setValue(friendList)
                            .addOnSuccessListener {
                                _validationMessage = ""
                                displayToast("Successfully added friend!")
                                loadAllFriends()
                            }
                            .addOnFailureListener {
                                _validationMessage = "Sorry! Something went wrong. " +
                                        "Please try again later."
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _validationMessage = "Sorry! Something went wrong. Please try again later."
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                _validationMessage = "Sorry! Something went wrong. Please try again later."
            }
        })
    }

    // Remove Friend
    fun removeFriend(friend: User) {
        friendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(friendsSnapshot: DataSnapshot) {
                var friendListId = ""
                var foundFriend = false

                // See if friend exists
                for (snapshot in friendsSnapshot.children) {
                    val friendId1 = snapshot.child("friendId1").value.toString()
                    val friendId2 = snapshot.child("friendId2").value.toString()

                    if ((friendId1 == currentUserUid && friendId2 == friend.userId) ||
                        (friendId2 == currentUserUid && friendId1 == friend.userId)) {
                        friendListId = snapshot.key.toString()
                        foundFriend = true
                    }

                    if (foundFriend) break
                }

                // Remove friend
                if (foundFriend) {
                    friendsReference.child(friendListId).removeValue()
                        .addOnSuccessListener {
                            displayToast("Successfully removed friend!")
                            loadAllFriends()
                        }
                        .addOnFailureListener {e -> displayToast("Error: $e") }
                }
            }

            override fun onCancelled(error: DatabaseError) { displayToast("Could not remove friend!") }
        })
    }

    // Display toast messages
    fun displayToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show()
    }
}