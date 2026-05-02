package com.myApp.vizualearnfinal.screens.register

import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.User

class RegisterModel(val app: CustomApplication) {

    fun saveNewUser(
        username: String, first: String, last: String, email: String,
        school: String, course: String, pass: String
    ) {
        val newUser = User(username, first, last, email, school, course, pass)

        app.registeredUser = newUser
    }
}