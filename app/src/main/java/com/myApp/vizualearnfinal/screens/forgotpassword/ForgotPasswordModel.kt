package com.myApp.vizualearnfinal.screens.forgotpassword

import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.User

class ForgotPasswordModel(private val app: CustomApplication) {

    fun getUser(username: String): User? {
        val user = app.registeredUser
        if (user?.username == username) return user
        return null
    }

    fun updatePassword(user: User, newPass: String) {
        user.password = newPass
        app.registeredUser = user
    }
}