/*package com.example.todolist.Analytics

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

    val completedTasks: LiveData<Int>
        get() = _completedTasks

    val incompleteTasks: LiveData<Int>
        get() = _incompleteTasks

    val completionPercentage: LiveData<Int>
        get() = _completionPercentage

    fun fetchTaskCompletionData() {
        val userId = currentUser?.uid ?: return
        val todayDateString = getCurrentDateString()

        val tasksQuery = tasksRef.orderByChild("createdAt")

        tasksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var completedCount = 0
                var totalTasksToday = 0

                snapshot.children.forEach { taskSnapshot ->
                    val taskData = taskSnapshot.value as? Map<String, Any>
                    if (taskData != null) {
                        val taskUserId = taskData["userId"] as? String
                        val taskFriend = taskData["friend"] as? String
                        val isCompleted = taskData["completed"] as? Boolean ?: false
                        //val createdAt = taskData["createdAt"] as? Long ?: 0
                        val dueDate = taskData["dueDate"] as? String

                        // Check if the dueDate is today and belongs to the user or friend
                        if (dueDate == todayDateString &&
                            (taskUserId == userId || taskFriend == userId)) {
                            totalTasksToday++
                            if (isCompleted) {
                                completedCount++
                            }
                        }
                    }
                }

                // Update LiveData with completion data
                _completedTasks.value = completedCount
                _incompleteTasks.value = totalTasksToday - completedCount

                // Calculate completion percentage
                val completionPercentage = if (totalTasksToday > 0) {
                    (completedCount.toDouble() / totalTasksToday.toDouble()) * 100.0
                } else {
                    0.0
                }

                // Update LiveData with completion percentage
                _completionPercentage.value = completionPercentage.toInt()
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
}
 */

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

    val completedTasks: LiveData<Int>
        get() = _completedTasks

    val incompleteTasks: LiveData<Int>
        get() = _incompleteTasks

    val completionPercentage: LiveData<Int>
        get() = _completionPercentage

    val tasksForTodayExist: LiveData<Boolean>
        get() = _tasksForTodayExist

    fun fetchTaskCompletionData() {
        val userId = currentUser?.uid ?: return
        val todayDateString = getCurrentDateString()

        val tasksQuery = tasksRef.orderByChild("createdAt")

        tasksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var completedCount = 0
                var totalTasksToday = 0

                snapshot.children.forEach { taskSnapshot ->
                    val taskData = taskSnapshot.value as? Map<String, Any>
                    if (taskData != null) {
                        val taskUserId = taskData["userId"] as? String
                        val taskFriend = taskData["friend"] as? String
                        val isCompleted = taskData["completed"] as? Boolean ?: false
                        val createdAt = taskData["createdAt"] as? Long ?: 0
                        val dueDate = taskData["dueDate"] as? String

                        if (dueDate == todayDateString &&
                            (taskUserId == userId || taskFriend == userId)) {
                            totalTasksToday++
                            if (isCompleted) {
                                completedCount++
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
}

