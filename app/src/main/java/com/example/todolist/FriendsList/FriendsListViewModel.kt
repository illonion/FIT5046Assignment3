package com.example.todolist.FriendsList

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.todolist.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsListViewModel: ViewModel() {
    private val currentUser = Firebase.auth.currentUser
    private val currentUserUid = currentUser?.uid
    private val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val mDatabase = database.reference
    private val friendsReference = mDatabase.child("friends")
    private val usersReference = mDatabase.child("users")
    private var _validationMessage = ""

    private var friendListUserIds = mutableListOf<String>()
    private var friendListUsers = mutableListOf<User>()

    val getValidationMessage: String
        get() = _validationMessage

    val getFriendsListUsers: MutableList<User>
        get() = friendListUsers

    fun loadAllFriends() {
        friendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(friendsSnapshot: DataSnapshot) {
                Log.i("FriendsListViewModel", "Do We Get Here 1")
                val newFriendListUserIds = mutableListOf<String>() // Temporary list to hold updated friends ids
                val newFriendListUsers = mutableListOf<User>() // Temporary list to hold updated friends

                for (snapshot in friendsSnapshot.children) {
                    val friendId1 = snapshot.child("friendId1").value.toString()
                    val friendId2 = snapshot.child("friendId2").value.toString()

                    if (friendId1 == currentUserUid) { newFriendListUserIds.add(friendId2) }
                    else if (friendId2 == currentUserUid) { newFriendListUserIds.add(friendId1) }

                    Log.i("FriendsListViewModel", newFriendListUserIds.size.toString())
                }

                Log.i("FriendsListViewModel", "Do We Get Here 2")
                Log.i("FriendsListViewModel", newFriendListUserIds.size.toString())

                friendListUserIds = newFriendListUserIds

                usersReference.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(usersSnapshot: DataSnapshot) {
                        Log.i("FriendsListViewModel", "Do We Get Here 3")
                        if (usersSnapshot.exists()) {
                            Log.i("FriendsListViewModel", "Do We Get Here 4")
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

                                Log.i("FriendsListViewModel", newFriendListUsers.size.toString())
                            }

                            // Update friendListUsers with the new list of friends
                            friendListUsers = newFriendListUsers
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _validationMessage = "Something went wrong with loading your friends! Please try again later."
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                _validationMessage = "Something went wrong with loading your friends! Please try again later."
            }
        })
    }
}