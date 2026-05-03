package com.myApp.vizualearnfinal.screens.editprofile

import com.myApp.vizualearnfinal.application.CustomApplication

class EditProfileModel(private val app: CustomApplication) {

    fun getCurrentUser() = app.loginUser

    fun updateCurrentUser(first: String, last: String, email: String, school: String, course: String, address: String) {
        val currentUser = app.loginUser
        if (currentUser != null) {
            // Create a new updated user object, keeping their original username, password, and streak
            val updatedUser = currentUser.copy(
                firstName = first,
                lastName = last,
                email = email,
                school = school,
                course = course,
                address = address  // ← ADD THIS LINE
            )
            app.loginUser = updatedUser
            app.registeredUser = updatedUser // Update the "database" too
        }
    }
}