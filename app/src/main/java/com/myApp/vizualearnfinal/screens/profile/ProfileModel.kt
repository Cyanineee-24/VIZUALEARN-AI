package com.myApp.vizualearnfinal.screens.profile

import com.myApp.vizualearnfinal.application.CustomApplication

class ProfileModel(private val app: CustomApplication) {
    fun getUsername() = app.loginUser?.username ?: ""
    fun getFirstName() = app.loginUser?.firstName ?: ""
    fun getLastName() = app.loginUser?.lastName ?: ""
    fun getEmail() = app.loginUser?.email ?: ""
    fun getSchool() = app.loginUser?.school ?: ""
    fun getCourse() = app.loginUser?.course ?: ""

    fun clearSession() { // Tentative, I might not implement this or maybe puhon!
        // Logging out, set session to null.
        app.loginUser = null
    }
}