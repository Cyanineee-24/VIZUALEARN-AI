package com.myApp.vizualearnfinal.screens.login

import com.myApp.vizualearnfinal.application.CustomApplication

class LoginModel(val app: CustomApplication) {

    fun validateCredentials(username: String, password: String): Boolean {

        val savedUser = app.registeredUser
        return savedUser != null && savedUser.username == username && savedUser.password == password
    }


    fun saveLoginSession(username: String) {
        val savedUser = app.registeredUser
        if (savedUser != null && savedUser.username == username) {
            app.loginUser = savedUser // The session is now active!
        }
    }
}