package com.example.todolist.Analytics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.LoginSignup.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
class SevenDayViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val tasksRef = database.getReference("tasks")
    private val currentUser = AuthenticationActivity().getUser()

    private val _tasksForLastWeekExist = MutableLiveData(false)
    private val _tagDistributionPercentage = MutableLiveData<Map<String, Double>>()

    val tasksForLastWeekExists: LiveData<Boolean>
        get() = _tasksForLastWeekExist

    val tagDistributionPercentage: LiveData<Map<String, Double>>
        get() = _tagDistributionPercentage

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTaskTagDistribution() {
        val userId = currentUser?.uid ?: return

        // Get the date of a week ago
        val weekAgoDate = LocalDate.now().minusWeeks(1)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val tasksQuery = tasksRef.orderByChild("createdAt")

        tasksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalTasksLastWeek = 0
                val tagCountsLastWeek = HashMap<String, Int>()

                snapshot.children.forEach { taskSnapshot ->
                    val taskData = taskSnapshot.value as? Map<*, *>
                    if (taskData != null) {
                        val taskUserId = taskData["userId"] as? String
                        val taskFriend = taskData["friend"] as? String
                        val taskTag = taskData["tag"] as? String
                        val dueDateStr = taskData["dueDate"] as? String

                        // Convert due date string to LocalDate object
                        val dueDate = LocalDate.parse(dueDateStr, formatter)

                        // Check if task is within the past week
                        if (dueDate != null && dueDate.isAfter(weekAgoDate) &&
                            (taskUserId == userId || taskFriend == userId)) {
                            totalTasksLastWeek++

                            tagCountsLastWeek[taskTag ?: "No Tag"] = (tagCountsLastWeek[taskTag ?: "No Tag"] ?: 0) + 1
                        }
                    }
                }

                _tasksForLastWeekExist.value = totalTasksLastWeek > 0

                val tagDistributionPercentage = mutableMapOf<String, Double>()
                tagCountsLastWeek.forEach { (tag, count) ->
                    tagDistributionPercentage[tag] = 0.0
                    if (totalTasksLastWeek > 0) {
                        val percentage = (count.toDouble() / totalTasksLastWeek.toDouble()) * 100.0
                        tagDistributionPercentage[tag] = percentage
                    }
                }

                _tagDistributionPercentage.value = tagDistributionPercentage
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}