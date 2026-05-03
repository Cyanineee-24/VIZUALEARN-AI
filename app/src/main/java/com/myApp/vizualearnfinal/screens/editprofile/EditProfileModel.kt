package com.myApp.vizualearnfinal.screens.editprofile

import android.content.Context
import android.content.SharedPreferences

class EditProfileModel(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun getCurrentData(): Map<String, String> {
        val fullName = prefs.getString("USERNAME", "") ?: ""
        val names = fullName.split(" ", limit = 2)
        val first = if (names.isNotEmpty()) names[0] else ""
        val last = if (names.size > 1) names[1] else ""

        return mapOf(
            "FIRST" to first,
            "LAST" to last,
            "EMAIL" to (prefs.getString("EMAIL", "") ?: ""),
            "SCHOOL" to (prefs.getString("SCHOOL", "") ?: ""),
            "COURSE" to (prefs.getString("COURSE", "") ?: ""),
            "ADDRESS" to (prefs.getString("ADDRESS", "") ?: "")
        )
    }

    fun saveChanges(first: String, last: String, email: String, school: String, course: String, address: String) {
        prefs.edit()
            .putString("USERNAME", "$first $last".trim())
            .putString("EMAIL", email)
            .putString("SCHOOL", school)
            .putString("COURSE", course)
            .putString("ADDRESS", address)
            .apply()
    }
}