package com.myApp.vizualearnfinal.screens.forgotpassword

interface ForgotPasswordContract {
    interface View {
        fun showSuccessMessage()
        fun showUserNotFoundMessage()
        fun showPasswordMismatchMessage()
        fun showEmptyFieldsMessage()
        fun navigateBackToLogin()
    }

    interface Presenter {
        fun updatePassword(username: String, newPass: String, confirmPass: String)
    }
}