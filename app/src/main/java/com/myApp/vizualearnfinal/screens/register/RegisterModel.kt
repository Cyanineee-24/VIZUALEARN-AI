package com.myApp.vizualearnfinal.screens.register

import android.content.Context
import android.os.Build
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.User

// FIX: Added Context so we can access SharedPreferences
class RegisterModel(private val context: Context, val app: CustomApplication) {

    fun saveNewUser(
        username: String,
        first: String,
        last: String,
        email: String,
        address: String,
        school: String,
        course: String,
        pass: String
    ) {
        // 1. Save to temporary app state
        val newUser = User(username, first, last, email, school, course, pass, address)
        app.registeredUser = newUser

        // 2. Calculate current Month and Year for "Member Since"
        val memberSinceDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = java.time.LocalDate.now()
            val month = current.month.name.lowercase().replaceFirstChar { it.uppercase() }
            "$month ${current.year}"
        } else {
            "May 2026" // Fallback
        }

        // 3. SAVE TO PERMANENT MEMORY!
        val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("USERNAME", "$first $last") // Combine for the display name
            .putString("EMAIL", email)
            .putString("SCHOOL", school)
            .putString("COURSE", course)
            .putString("ADDRESS", address)
            .putString("MEMBER_SINCE", memberSinceDate)
            .apply()
    }
}