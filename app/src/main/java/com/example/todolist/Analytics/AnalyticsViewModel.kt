package com.example.todolist.Analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val tasksRef = database.getReference("tasks")
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val _completedTasks = MutableLiveData<Int>(0)
    private val _incompleteTasks = MutableLiveData<Int>(0)
    private val _completionPercentage = MutableLiveData<Int>(0)
    private val _tasksForTodayExist = MutableLiveData<Boolean>(false)
    private val _yesterdayCompletionPercentage = MutableLiveData<Int>(0)

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

    fun fetchTaskCompletionData() {
        val userId = currentUser?.uid ?: return
        val todayDateString = getCurrentDateString()
        val yesterdayDateString = getYesterdayDateString()

        val tasksQuery = tasksRef.orderByChild("createdAt")

        tasksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var completedCount = 0
                var totalTasksToday = 0
                var completedCountYesterday = 0
                var totalTasksYesterday = 0

                snapshot.children.forEach { taskSnapshot ->
                    val taskData = taskSnapshot.value as? Map<String, Any>
                    if (taskData != null) {
                        val taskUserId = taskData["userId"] as? String
                        val taskFriend = taskData["friend"] as? String
                        val isCompleted = taskData["completed"] as? Boolean ?: false
                        val createdAt = taskData["createdAt"] as? Long ?: 0
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

                val completionPercentage = if (totalTasksToday > 0) {
                    (completedCount.toDouble() / totalTasksToday.toDouble()) * 100.0
                } else {
                    0.0
                }

                _completionPercentage.value = completionPercentage.toInt()

                val yesterdayCompletionPercentage = if (totalTasksYesterday > 0) {
                    (completedCountYesterday.toDouble() / totalTasksYesterday.toDouble()) * 100.0
                } else {
                    0.0
                }

                _yesterdayCompletionPercentage.value = yesterdayCompletionPercentage.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1) // Get yesterday's date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(cal.time)
    }
}