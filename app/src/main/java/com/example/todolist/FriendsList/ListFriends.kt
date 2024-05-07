package com.example.todolist.FriendsList

import android.util.Log
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
import com.example.todolist.User
import com.example.todolist.ui.theme.Purple40
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

// List of to do list items
@Composable
fun ListFriends(friend: User, friendsReference: DatabaseReference, onFriendDeleted: () -> Unit) {
    val context = LocalContext.current
    Log.i("Name", friend.firstName)
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
                Text(
                    text = "${friend.firstName} ${friend.lastName}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 36.sp
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            friendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(friendsSnapshot: DataSnapshot) {
                                    var friendListId = ""
                                    var foundFriend = false
                                    for (snapshot in friendsSnapshot.children) {

                                        val friendId1 = snapshot.child("friendId1").value.toString()
                                        val friendId2 = snapshot.child("friendId1").value.toString()

                                        if ((friendId1 == Firebase.auth.uid && friendId2 == friend.userId) ||
                                            (friendId2 == Firebase.auth.uid && friendId1 == friend.userId)) {
                                            friendListId = snapshot.key.toString()
                                            foundFriend = true
                                        }

                                        if (foundFriend) break
                                    }

                                    if (foundFriend) {
                                        friendsReference.child(friendListId).removeValue()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Successfully removed friend!", Toast.LENGTH_LONG).show()
                                                onFriendDeleted()
                                            }
                                            .addOnFailureListener {e -> Toast.makeText(context, "Error: $e", Toast.LENGTH_LONG).show() }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Could not remove friend!", Toast.LENGTH_LONG).show()
                                }
                            })
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
}