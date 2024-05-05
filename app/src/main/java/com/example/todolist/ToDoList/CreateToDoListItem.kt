import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todolist.Navigation.Routes
import com.example.todolist.ToDoList.ToDoListItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Friend(val name: String, val uid: String)

@SuppressLint("SimpleDateFormat", "MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToDoListItem(navController: NavHostController) {
    // Current context
    val context = LocalContext.current

    // Firebase
    val currentUser = Firebase.auth.currentUser
    val currentUserUid = currentUser?.uid
    val database = FirebaseDatabase.getInstance("https://fit5046-assignment-3-5083c-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val mDatabase = database.reference
    val friendsRef = mDatabase.child("friends")
    val usersRef = mDatabase.child("users")

    // Task
    var toDoItem by remember { mutableStateOf("") }

    // Tag
    var tagsIsExpanded by remember { mutableStateOf(false) }
    val tags = listOf("No Tag", "Indoors", "Outdoors", "School", "Work", "Sports")
    var selectedTag by remember { mutableStateOf(tags[0]) }

    // Calendar
    val calendar = Calendar.getInstance()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableLongStateOf(calendar.timeInMillis) }
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    // Friends
    val friends by remember { mutableStateOf(mutableListOf(Friend("No One", ""))) }
    val friendsUids = mutableListOf("")
    var friendIsExpanded by remember { mutableStateOf(false) }
    var selectedFriend by remember { mutableStateOf(friends[0]) }

    fun fetchFriends() {
        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(usersSnapshot: DataSnapshot) {
                        for (friendUid in friendsUids) {
                            // Skip first friendUid
                            numberOfElementsPassed++
                            if (numberOfElementsPassed == 1) continue

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
                        TODO("Not yet implemented")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // Learned LaunchedEffect from https://medium.com/@sujathamudadla1213/what-is-launchedeffect-coroutine-api-android-jetpack-compose-76d568b79e63
    LaunchedEffect(Unit) {
        fetchFriends()
    }

    // Top Bar
    TopAppBar(
        title = { Text(text = "Add Task") },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("ToDoList") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
            MaterialTheme.colorScheme.primaryContainer,
           titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(60.dp))
        // Outlined Text Field for task
        OutlinedTextField(
            value = toDoItem,
            onValueChange = {toDoItem = it},
            label = { Text("Task Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Exposed drop down menu for tags
        ExposedDropdownMenuBox(
            expanded = tagsIsExpanded,
            onExpandedChange = { tagsIsExpanded = it },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .focusProperties { canFocus = false }
                    .padding(bottom = 8.dp),
                readOnly = true,
                value = selectedTag,
                onValueChange = {},
                label = { Text("Tag") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagsIsExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = tagsIsExpanded,
                onDismissRequest = { tagsIsExpanded = false }
            ) {
                tags.forEach {
                        selectedOption -> DropdownMenuItem(
                    text = { Text(selectedOption) },
                    onClick = {
                        selectedTag = selectedOption
                        tagsIsExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Date Picker
        if (showDatePicker) {
            DatePickerDialog (
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            selectedDate = datePickerState.selectedDateMillis!!
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Thank you so much https://caelis.medium.com/jetpack-compose-datepicker-textfield-39808e42646a
        Box {
            OutlinedTextField(
                value = formatter.format(Date(selectedDate)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Due Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                trailingIcon = {
                    // Use a calendar icon as the trailing icon
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Select Date",
                    )
                }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0f)
                    .clickable(onClick = { showDatePicker = true }),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Friend
        ExposedDropdownMenuBox(
            expanded = friendIsExpanded,
            onExpandedChange = { friendIsExpanded = it },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .focusProperties { canFocus = false }
                    .padding(bottom = 8.dp),
                readOnly = true,
                value = selectedFriend.name,
                onValueChange = {},
                label = { Text("Friend") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = friendIsExpanded) // Use friendIsExpanded here
                }
            )
            ExposedDropdownMenu(
                expanded = friendIsExpanded,
                onDismissRequest = { friendIsExpanded = false }
            ) {
                friends.forEach { friend ->
                    DropdownMenuItem(
                        text = { Text(friend.name) },
                        onClick = {
                            selectedFriend = friend
                            friendIsExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Add item button
        Button(
            onClick = {
                if (InputValidation().isValidTaskName(toDoItem)) {
                    navController.navigate(Routes.ToDoList.value)
                    val itemId = "task_" + UUID.randomUUID().toString()

                    // Get date in readable format
                    val instant = Instant.ofEpochMilli(selectedDate)
                    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val item = currentUserUid?.let { ToDoListItem(itemId, it, toDoItem, selectedTag, date.format(format), selectedFriend.uid, false, System.currentTimeMillis()) }

                    mDatabase.child("tasks").child(itemId).setValue(item)
                        .addOnSuccessListener {
                            navController.navigate(Routes.ToDoList.value)
                            Toast.makeText(context,"Successfully created Task!",Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e -> Toast.makeText(context,"Error $e!",Toast.LENGTH_LONG).show()}
                } else {
                    Toast.makeText(
                        context,
                        "INVALID INPUT: Please enter the Task Name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text("Add Task")
        }
    }
}