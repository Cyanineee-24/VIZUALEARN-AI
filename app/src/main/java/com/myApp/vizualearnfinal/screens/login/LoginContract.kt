package com.myApp.vizualearnfinal.screens.login

class LoginContract {
    interface View{
        fun showSuccessMessage()
        fun showInvalidCredentialsMessage()
        fun showEmptyFieldsMessage()
        fun navigateToRegisterScreen()
        fun navigateToDashboardScreen()
    }

    interface Presenter{
        fun login(username: String, password: String)
        fun createAccount()
    }
}