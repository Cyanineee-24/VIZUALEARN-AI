package com.myApp.vizualearnfinal.screens.profile

interface ProfileContract {
    interface View {
        fun displayUserProfile(username: String, email: String, school: String, course: String, address: String)
        fun displayStats(mindMapsCount: Int, cardsCount: Int) // NEW: For the database stats
        fun navigateBack()
        fun navigateToLogin()
    }

    interface Presenter {
        fun loadProfileData()
        fun handleSignOut()
    }
}