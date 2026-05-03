package com.myApp.vizualearnfinal.screens.profile

import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class ProfileModel(private val app: CustomApplication, private val repository: StudySetRepository) {
    fun getUsername() = app.loginUser?.username ?: ""
    fun getFirstName() = app.loginUser?.firstName ?: ""
    fun getLastName() = app.loginUser?.lastName ?: ""
    fun getEmail() = app.loginUser?.email ?: ""
    fun getSchool() = app.loginUser?.school ?: ""
    fun getCourse() = app.loginUser?.course ?: ""

    // NEW: Get Address (Fallback to Argao, Cebu if blank for now)
    fun getAddress(): String {
        val addr = app.loginUser?.address
        return if (addr.isNullOrEmpty()) "Argao, Cebu" else addr
    }

    // NEW: Calculate the real database stats!
    suspend fun getDatabaseStats(): Pair<Int, Int> {
        val sets = repository.getAllSets()
        val totalMindMaps = sets.sumOf { it.mindMapCount }
        val totalCards = sets.sumOf { it.cardCount }
        return Pair(totalMindMaps, totalCards)
    }

    fun clearSession() {
        app.loginUser = null
    }
}