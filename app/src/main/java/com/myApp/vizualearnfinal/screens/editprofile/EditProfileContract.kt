package com.myApp.vizualearnfinal.screens.editprofile

interface EditProfileContract {
    interface View {
        fun populateFields(first: String, last: String, email: String, school: String, course: String, address: String)
        fun showMessage(message: String)
        fun finishActivity()
    }

    interface Presenter {
        fun loadCurrentData()
        fun saveChanges(first: String, last: String, email: String, school: String, course: String, address: String)
    }
}