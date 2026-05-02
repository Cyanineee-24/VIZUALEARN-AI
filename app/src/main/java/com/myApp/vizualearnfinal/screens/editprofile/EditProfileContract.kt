package com.myApp.vizualearnfinal.screens.editprofile

interface EditProfileContract {
    interface View {
        fun populateUserData(first: String, last: String, email: String, school: String, course: String, address: String)
        fun showMessage(message: String)
        fun navigateBackToProfile()
    }

    interface Presenter {
        fun loadCurrentUserData()
        fun saveChanges(first: String, last: String, email: String, school: String, course: String, address: String)
    }
}