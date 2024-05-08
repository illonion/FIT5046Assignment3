package com.example.todolist.FriendsList

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolist.DatabaseActivity
import com.example.todolist.Navigation.Routes
import com.example.todolist.User
import com.example.todolist.ui.theme.Purple40

// List of to do list items
@Composable
fun ListFriends(navController: NavHostController, friendsListViewModel : FriendsListViewModel, friend: User) {

    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(3.dp),
            shape = RoundedCornerShape(60.dp),
        ) {
            Row(
                modifier = Modifier.padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Friend's name
                Text(
                    text = "${friend.firstName} ${friend.lastName}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 36.sp
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
                // Button
                IconButton(
                    onClick = {
                        // Delete item
                        DatabaseActivity().checkValidSession { isValidSession ->
                            if (isValidSession) {
                                friendsListViewModel.removeFriend(friend)
                                navController.navigate("FriendsList")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Session Expired, please log in again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(Routes.MainLogout.value)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Purple40,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}