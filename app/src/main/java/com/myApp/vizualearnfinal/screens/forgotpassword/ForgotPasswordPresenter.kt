package com.myApp.vizualearnfinal.screens.forgotpassword

import com.myApp.vizualearnfinal.application.CustomApplication

class ForgotPasswordPresenter(
    private val view: ForgotPasswordContract.View,
    private val model: ForgotPasswordModel,
    private val app: CustomApplication
) : ForgotPasswordContract.Presenter {

    override fun updatePassword(username: String, newPass: String, confirmPass: String) {
        if (username.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            view.showEmptyFieldsMessage()
            return
        }

        if (newPass != confirmPass) {
            view.showPasswordMismatchMessage()
            return
        }

        val registeredUser = app.registeredUser
        if (registeredUser != null && registeredUser.username == username) {
            // Update the password in the session/application state
            registeredUser.password = newPass
            app.registeredUser = registeredUser
            
            view.showSuccessMessage()
            view.navigateBackToLogin()
        } else {
            view.showUserNotFoundMessage()
        }
    }
}