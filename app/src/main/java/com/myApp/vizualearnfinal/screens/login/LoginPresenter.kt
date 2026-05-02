package com.myApp.vizualearnfinal.screens.login

import android.content.Intent
import com.myApp.vizualearnfinal.screens.profile.ProfileActivity

class LoginPresenter(
    val view: LoginContract.View,
    val model: LoginModel): LoginContract.Presenter {
    override fun login(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {

            // Validate the credentials
            if (model.validateCredentials(username, password)) {

                // Save the active session BEFORE navigating!
                model.saveLoginSession(username)

                // Show success and navigate
                view.showSuccessMessage()
                view.navigateToDashboardScreen()

            } else {
                view.showInvalidCredentialsMessage()
            }
        } else {
            view.showEmptyFieldsMessage()
        }
    }

    override fun createAccount() {
        view.navigateToRegisterScreen()
    }


}