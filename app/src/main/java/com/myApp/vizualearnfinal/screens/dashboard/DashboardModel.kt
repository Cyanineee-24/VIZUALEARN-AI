package com.myApp.vizualearnfinal.screens.dashboard

import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class DashboardModel(
    private val repository: StudySetRepository,
    private val app: CustomApplication
) {
    suspend fun getAllSets(): List<StudySet> {
        return repository.getAllSets()
    }

    fun getUserName(): String {
        val user = app.loginUser
        return if (user != null) {
            "${user.firstName} ${user.lastName}"
        } else {
            "Guest"
        }
    }

    fun getUserStreak(): Int {
        // Hardcoded for now since streak isn't a variable in our User data class yet!
        return 14
    }
}