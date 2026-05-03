package com.myApp.vizualearnfinal.screens.profile

import android.content.Context
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class ProfileModel(private val context: Context, private val app: CustomApplication, private val repository: StudySetRepository) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun getUsername() = prefs.getString("USERNAME", "") ?: ""
    fun getEmail() = prefs.getString("EMAIL", "") ?: ""
    fun getSchool() = prefs.getString("SCHOOL", "CIT - U") ?: ""
    fun getCourse() = prefs.getString("COURSE", "Computer Science") ?: ""
    fun getAddress() = prefs.getString("ADDRESS", "Argao, Cebu") ?: ""
    fun getMemberSince() = prefs.getString("MEMBER_SINCE", "May 2026") ?: ""

    suspend fun getDatabaseStats(): Pair<Int, Int> {
        val sets = repository.getAllSets()
        val totalMindMaps = sets.sumOf { it.mindMapCount }
        val totalCards = sets.sumOf { it.cardCount }
        return Pair(totalMindMaps, totalCards)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        app.loginUser = null
    }
}