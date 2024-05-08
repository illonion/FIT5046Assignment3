package com.example.todolist.ToDoList

import Friend
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoListItemViewModel(application: Application) : AndroidViewModel(application) {
    // Database
    private val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val taskReference = database.getReference("tasks")
    private val friendsReference = database.getReference("friends")
    private val usersReference = database.getReference("users")
    private val repository: ToDoListItemRepository

    // Friends
    var friends = mutableListOf(Friend("No One", ""))
    val friendsUids = mutableListOf("")

    // Current user
    val currentUserUid = Firebase.auth.currentUser?.uid

    init{
        repository = ToDoListItemRepository(application)
    }

    val allToDoListItems: LiveData<List<ToDoListItem>> = repository.allToDoListItems.asLiveData()

    // Update to do list item
    fun updateToDoListItem(toDoListItem: ToDoListItem) = viewModelScope.launch(Dispatchers.IO) {
        // Convert to do list item to a map (such that it can be updated by firebase)
        val toDoListItemMap = mapOf(
            "completed" to toDoListItem.completed,
            "createdAt" to toDoListItem.createdAt,
            "dueDate" to toDoListItem.dueDate,
            "friend" to toDoListItem.friend,
            "name" to toDoListItem.name,
            "tag" to toDoListItem.tag,
            "taskId" to toDoListItem.taskId,
            "userId" to toDoListItem.userId
        )
        taskReference.child(toDoListItem.taskId).updateChildren(toDoListItemMap)
        repository.update(toDoListItem)
    }

    // Delete to do list item
    fun deleteToDoListItem(toDoListItem: ToDoListItem) = viewModelScope.launch(Dispatchers.IO) {
        taskReference.child(toDoListItem.taskId).removeValue()
            .addOnSuccessListener {
                syncDataFromFirebase()
                makeToast("Successfully deleted item!")
            }
            .addOnFailureListener {
                makeToast("Failed to deleted item!")
            }
    }

    // Sync firebase database with room database
    fun syncDataFromFirebase() {
        taskReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val listOfToDoListItems = mutableListOf<ToDoListItem>()
                    for (snapshot in dataSnapshot.children) {
                        val taskId = snapshot.key.toString()
                        val userId = snapshot.child("userId").value.toString()
                        val name = snapshot.child("name").value.toString()
                        val tag = snapshot.child("tag").value.toString()
                        val dueDate = snapshot.child("dueDate").value.toString()
                        val friend = snapshot.child("friend").value.toString()
                        val completed = convertToBoolean(snapshot.child("completed").value)
                        val createdAt = snapshot.child("createdAt").value.toString().toLong()

                        listOfToDoListItems.add(ToDoListItem(taskId,userId,name,tag,dueDate,friend,completed,createdAt))
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        repository.clearAllToDoListItems()
                        repository.insertAllToDoListItems(listOfToDoListItems)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast("An error happened with the database! Please try again later.")
            }
        })
    }

    // mark to do list item as complete
    fun markItemAsCompleted(taskId: String) {
        taskReference.child(taskId).child("completed").setValue(true).addOnCompleteListener {
            if (it.isSuccessful) {
                syncDataFromFirebase()
                makeToast("Successfully marked item as complete!")
            } else {
                makeToast("Failed to mark item as complete!")
            }
        }
    }

    // Get friends list
    val getFriendsList: MutableList<Friend>
        get() = friends

    // Get all friends
    fun fetchFriends() {
        // Reset friends list
        friends = mutableListOf(Friend("No One", ""))

        friendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (friendsSnapshot in dataSnapshot.children) {
                    // Add friend uis
                    val friendId1 = friendsSnapshot.child("friendId1").value.toString()
                    val friendId2 = friendsSnapshot.child("friendId2").value.toString()
                    if (friendId1 == currentUserUid) {
                        friendsUids.add(friendId2)
                    } else if (friendId2 == currentUserUid) {
                        friendsUids.add(friendId1)
                    }
                }

                var numberOfElementsPassed = 0
                usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(usersSnapshot: DataSnapshot) {
                        for (friendUid in friendsUids) {
                            // Skip first friendUid
                            numberOfElementsPassed++
                            if (numberOfElementsPassed == 1) continue

                            // Check if the user exists
                            val userSnapshot = usersSnapshot.child(friendUid)
                            if (userSnapshot.exists()) {
                                val friendName =
                                    userSnapshot.child("firstName").value.toString() + " " + userSnapshot.child(
                                        "lastName"
                                    ).value.toString()
                                val currentFriendUid = userSnapshot.key.toString()
                                if (friendName.trim().isNotBlank()) {
                                    friends.add(Friend(friendName, currentFriendUid))
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        makeToast("An error happened with the database! Please try again later.")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast("An error happened with the database! Please try again later.")
            }
        })
    }

    // Make a toast
    private fun makeToast(message: String) { Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show() }
}

fun convertToBoolean(value: Any?): Boolean {
    return when (value) {
        is Boolean -> value
        is String -> value.toBoolean()
        is Number -> value.toInt() != 0
        else -> false
    }
}