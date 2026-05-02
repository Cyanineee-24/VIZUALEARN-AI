package com.myApp.vizualearnfinal.screens.profile

interface ProfileContract {
    interface View {
        fun displayUserProfile(username: String, email: String, school: String, course: String)
        fun navigateBack()
        fun navigateToLogin()
    }

    interface Presenter {
        fun loadProfileData()
        fun handleSignOut()
    }
}