package com.myApp.vizualearnfinal.screens.register

interface RegisterContract {
    interface View {
        fun showSuccessMessage()
        fun showEmptyFieldsMessage()
        fun showPasswordMismatchMessage()
        fun showTermsNotAcceptedMessage()
        fun navigateBackToLogin()
    }

    interface Presenter {

        fun register(
            username: String,
            first: String, last: String, email: String, address: String,
            school: String, course: String,
            pass: String, confirmPass: String,
            isTermsChecked: Boolean
        )
    }
}