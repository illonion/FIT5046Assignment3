import android.os.Build
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todolist.Routes
import com.example.todolist.ToDoListItem
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToDoListItem(navController: NavHostController) {

    var toDoItem by remember { mutableStateOf("") }

    var tagsIsExpanded by remember { mutableStateOf(false) }
    val tags = listOf("No Tag", "Indoors", "Outdoors", "School", "Work", "Sports")
    var selectedTag by remember { mutableStateOf(tags[0]) }

    val calendar = Calendar.getInstance()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    var friendIsExpanded by remember { mutableStateOf(false) }
    val friends = listOf("No one", "Alec", "Jimmy", "Milly", "Lawrence")
    var selectedFriend by remember { mutableStateOf(friends[0]) }

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
            label = { Text("To Do Item") },
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

        //
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
                value = selectedFriend,
                onValueChange = {},
                label = { Text("Friend") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagsIsExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = friendIsExpanded,
                onDismissRequest = { friendIsExpanded = false }
            ) {
                friends.forEach {
                        selectedOption -> DropdownMenuItem(
                    text = { Text(selectedOption) },
                    onClick = {
                        selectedFriend = selectedOption
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
                navController.navigate(Routes.ToDoList.value)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text("Add Task")
        }
    }
}