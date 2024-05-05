package com.example.todolist

/*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// ViewModel for managing analytics data related to task completion
class AnalyticsViewModel : ViewModel() {
    // Firebase references and authentication
    private val database = FirebaseDatabase.getInstance() // Get instance of Firebase Database
    private val tasksRef = database.getReference("tasks") // Reference to 'tasks' node in the database
    private val auth = FirebaseAuth.getInstance() // Get instance of Firebase Auth
    private val currentUser = auth.currentUser // Get current authenticated user

    // LiveData to hold counts of completed and incomplete tasks
    private val _completedTasks = MutableLiveData<Int>(0) // Mutable LiveData for completed tasks count, initialised to 0
    private val _incompleteTasks = MutableLiveData<Int>(0) // Mutable LiveData for incomplete tasks count, initialised to 0
    private val _completionPercentage = MutableLiveData<Int>(0) // LiveData for completion percentage

    // Expose completedTasks as LiveData to observe the chances in completed tasks count
    val completedTasks: LiveData<Int>
        get() = _completedTasks

    // Expose incompleteTasks as LiveData to observe the chances in incomplete tasks count
    val incompleteTasks: LiveData<Int>
        get() = _incompleteTasks

    // Expose completion percentage as LiveData
    val completionPercentage: LiveData<Int>
        get() = _completionPercentage

    // Function to fetch and update task completion data from Firebase Realtime DB
    fun fetchTaskCompletionData() {
        // Get user ID of the current authenticated user, return if not authenticated
        val userId = currentUser?.uid ?: return

        // Construct a query to retrieve tasks for the current user ordered by 'userID'
        val userTasksQuery = tasksRef.orderByChild("userId").equalTo(userId)

        // Add a ValueEventListener to listen for data changes in the database
        userTasksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var completedCount = 0 // Counter for completed tasks
                var incompleteCount = 0 // Counter for incomplete tasks
                var totalTasksToday = 0 // Counter for amount of tasks for today

                // Iterate through each task snapshot in the database
                snapshot.children.forEach { taskSnapshot ->
                    // Retrieve 'completed' status of the task (default to false if not found)
                    val isCompleted = taskSnapshot.child("completed").getValue(Boolean::class.java) ?: false

                    // Increment respective counter based on task completion status
                    if (isCompleted) {
                        completedCount++
                    } else {
                        incompleteCount++
                    }
                }

                // Update LiveData values with the latest task completion counts
                _completedTasks.value = completedCount
                _incompleteTasks.value = incompleteCount
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
                error.toException().printStackTrace() // Print stack trace for debugging
            }
        })
    }
}
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class AnalyticsViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val tasksRef = database.getReference("tasks")
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val _completedTasks = MutableLiveData<Int>(0)
    private val _incompleteTasks = MutableLiveData<Int>(0)
    private val _completionPercentage = MutableLiveData<Int>(0)

    val completedTasks: LiveData<Int>
        get() = _completedTasks

    val incompleteTasks: LiveData<Int>
        get() = _incompleteTasks

    val completionPercentage: LiveData<Int>
        get() = _completionPercentage

    fun fetchTaskCompletionData() {
        val userId = currentUser?.uid ?: return
        val todayStartTimestamp = getTodayStartTimestamp()
        val tomorrowStartTimestamp = getTomorrowStartTimestamp()

        val userTasksQuery = tasksRef.orderByChild("userId").equalTo(userId)

        userTasksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var completedCount = 0
                var totalTasksToday = 0

                snapshot.children.forEach { taskSnapshot ->
                    val taskData = taskSnapshot.value as? Map<String, Any>

                    if (taskData != null) {
                        val isCompleted = taskData["completed"] as? Boolean ?: false
                        val createdAt = taskData["createdAt"] as? Long ?: 0

                        // Check if task was created today based on createdAt timestamp
                        if (createdAt in todayStartTimestamp..<tomorrowStartTimestamp) {
                            totalTasksToday++

                            if (isCompleted) {
                                completedCount++
                            }
                        }
                    }
                }

                _completedTasks.value = completedCount
                _incompleteTasks.value = totalTasksToday - completedCount

                // Calculate completion percentage
                val percentage = if (totalTasksToday > 0) {
                    (completedCount * 100) / totalTasksToday
                } else {
                    0
                }

                _completionPercentage.value = percentage
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    private fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getTomorrowStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

