package com.example.todolist.friendsList

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsList(navController: NavHostController, friendsListViewModel: FriendsListViewModel) {
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
                navController,
                onAdd = { email -> friendsListViewModel.addToFriendsList(email) }
            )

            // Display validation message
            if (friendsListViewModel.getValidationMessage != "") {
                Text(
                    text = friendsListViewModel.getValidationMessage,
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

    // Get friends list
    val currentFriendsListUsers = friendsListViewModel.getFriendsListUsers

    // Display friends list
    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(270.dp))
        LazyColumn {
            itemsIndexed(currentFriendsListUsers) { _, item ->
                Log.i("Friend", item.firstName)
                ListFriends(navController, friendsListViewModel, item)
            }
        }
    }
}