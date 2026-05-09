package com.myApp.vizualearnfinal.screens.login

class LoginPresenter(
    private val view: LoginContract.View,
    private val model: LoginModel
) : LoginContract.Presenter {

    override fun login(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            if (model.validateCredentials(username, password)) {
                model.saveLoginSession(username)
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

    override fun forgotPassword() {
        view.navigateToForgotPasswordScreen()
    }
}