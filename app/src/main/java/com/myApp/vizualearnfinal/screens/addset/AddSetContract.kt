package com.myApp.vizualearnfinal.screens.addset

class AddSetContract {
    interface View {
        fun showMessage(message: String)
        fun navigateBackToDashboard()
    }

    interface Presenter {
        fun saveStudySet(name: String, subject: String, iconResName: String, description: String)
    }
}