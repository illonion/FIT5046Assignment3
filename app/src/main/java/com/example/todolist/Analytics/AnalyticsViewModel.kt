package com.example.todolist.Analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.LoginSignup.AuthenticationActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel : ViewModel() {
    // Database
    private val database = FirebaseDatabase.getInstance()
    private val tasksRef = database.getReference("tasks")
    private var currentUser = AuthenticationActivity().getUser()

    // Task values
    private val _completedTasks = MutableLiveData(0)
    private val _incompleteTasks = MutableLiveData(0)
    private val _completionPercentage = MutableLiveData(0)
    private val _tasksForTodayExist = MutableLiveData(false)
    private val _yesterdayCompletionPercentage = MutableLiveData(0)

    val completedTasks: LiveData<Int>
        get() = _completedTasks

    val incompleteTasks: LiveData<Int>
        get() = _incompleteTasks

    val completionPercentage: LiveData<Int>
        get() = _completionPercentage

    val tasksForTodayExist: LiveData<Boolean>
        get() = _tasksForTodayExist

    val yesterdayCompletionPercentage: LiveData<Int>
        get() = _yesterdayCompletionPercentage

    // Fetch task completion data from database
    fun fetchTaskCompletionData() {
        currentUser = AuthenticationActivity().getUser()
        val userId = currentUser?.uid ?: return
        val todayDateString = getCurrentDateString()
        val yesterdayDateString = getYesterdayDateString()

        // Listen for changes in the data
        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var completedCount = 0
                var totalTasksToday = 0
                var completedCountYesterday = 0
                var totalTasksYesterday = 0

                // Iterate over each child snapshot
                snapshot.children.forEach { taskSnapshot ->
                    val taskData = taskSnapshot.value as? Map<*, *>
                    if (taskData != null) {
                        val taskUserId = taskData["userId"] as? String
                        val taskFriend = taskData["friend"] as? String
                        val isCompleted = taskData["completed"] as? Boolean ?: false
                        val dueDate = taskData["dueDate"] as? String

                        // Check if task is for today
                        if (dueDate == todayDateString &&
                            (taskUserId == userId || taskFriend == userId)) {
                            totalTasksToday++
                            if (isCompleted) {
                                completedCount++
                            }
                        }

                        // Check if task is for yesterday
                        if (dueDate == yesterdayDateString &&
                            (taskUserId == userId || taskFriend == userId)) {
                            totalTasksYesterday++
                            if (isCompleted) {
                                completedCountYesterday++
                            }
                        }
                    }
                }

                _completedTasks.value = completedCount
                _incompleteTasks.value = totalTasksToday - completedCount
                _tasksForTodayExist.value = totalTasksToday > 0

                // Get completion percentage for today and yesterday
                _completionPercentage.value = getCompletionPercentage(totalTasksToday, completedCount)
                _yesterdayCompletionPercentage.value = getCompletionPercentage(totalTasksYesterday, completedCountYesterday)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    // Get day completion percentage
    fun getCompletionPercentage(numberOfTasks: Int, totalTasks: Int): Int {
        var completionPercentage = 0.0
        if (numberOfTasks > 0) {
            completionPercentage = (totalTasks.toDouble() / numberOfTasks.toDouble()) * 100.0
        }
        return completionPercentage.toInt()
    }

    // Get current date string
    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    // Get yesterday's date string
    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1) // Get yesterday's date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(cal.time)
    }
}