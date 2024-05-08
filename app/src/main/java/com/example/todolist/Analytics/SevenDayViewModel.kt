package com.example.todolist.Analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.ui.theme.IncompleteGrey
import com.example.todolist.ui.theme.IndoorsPink
import com.example.todolist.ui.theme.OutdoorsGreen
import com.example.todolist.ui.theme.SchoolPurple
import com.example.todolist.ui.theme.SportsOrange
import com.example.todolist.ui.theme.WorkBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
class SevenDayViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val tasksRef = database.getReference("tasks")
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val _tasksForTodayExist = MutableLiveData<Boolean>(false)
    private val _tagDistributionPercentage = MutableLiveData<Map<String, Double>>()

    val tasksForTodayExist: LiveData<Boolean>
        get() = _tasksForTodayExist

    val tagDistributionPercentage: LiveData<Map<String, Double>>
        get() = _tagDistributionPercentage

    fun fetchTaskTagDistribution() {
        val userId = currentUser?.uid ?: return
        val todayDateString = getCurrentDateString()

        val tasksQuery = tasksRef.orderByChild("createdAt")

        tasksQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalTasksToday = 0
                var indoorsCount = 0
                var outdoorsCount = 0
                var workCount = 0
                var schoolCount = 0
                var sportsCount = 0
                var noTagCount = 0

                snapshot.children.forEach { taskSnapshot ->
                    val taskData = taskSnapshot.value as? Map<String, Any>
                    if (taskData != null) {
                        val taskUserId = taskData["userId"] as? String
                        val taskFriend = taskData["friend"] as? String
                        val taskTag = taskData["tag"] as? String
                        val dueDate = taskData["dueDate"] as? String

                        // Check if task is for today
                        if (dueDate == todayDateString &&
                            (taskUserId == userId || taskFriend == userId)) {
                            totalTasksToday++

                            when (taskTag) {
                                "Indoors" -> indoorsCount++
                                "Outdoors" -> outdoorsCount++
                                "Work" -> workCount++
                                "School" -> schoolCount++
                                "Sports" -> sportsCount++
                                "No Tag" -> noTagCount++
                            }
                        }
                    }
                }

                _tasksForTodayExist.value = totalTasksToday > 0

                val tagCounts = mapOf(
                    "Indoors" to indoorsCount,
                    "Outdoors" to outdoorsCount,
                    "Work" to workCount,
                    "School" to schoolCount,
                    "Sports" to sportsCount,
                    "No Tag" to noTagCount
                )

                val tagDistributionPercentage = mutableMapOf<String, Double>()
                tagCounts.forEach { (tag, count) ->
                    if (totalTasksToday > 0) {
                        val percentage = (count.toDouble() / totalTasksToday.toDouble()) * 100.0
                        tagDistributionPercentage[tag] = percentage
                    } else {
                        tagDistributionPercentage[tag] = 0.0
                    }
                }

                _tagDistributionPercentage.value = tagDistributionPercentage
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