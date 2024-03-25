package com.example.todolist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class FriendViewModel: ViewModel() {
    val friends: MutableState<List<Users>> =
        mutableStateOf(emptyList())
    fun addFriend(friend: Users) {
        friends.value = friends.value.toMutableList().apply()
        {
            add(friend)
        }
    }
    fun deleteFriend(index: Int) {
        if (index >= 0) {
            friends.value = friends.value.toMutableList().apply()
            { removeAt(index) }
        }
    }
}
