package com.myApp.vizualearnfinal.screens.profile

class ProfilePresenter(
    private val view: ProfileContract.View,
    private val model: ProfileModel
) : ProfileContract.Presenter {

    override fun loadProfileData() {
        // Fetch the username and format it with the @ symbol
        val formattedUsername = "@${model.getUsername()}"

        // Send the formatted username instead of the Full Name
        view.displayUserProfile(
            formattedUsername,
            model.getEmail(),
            model.getSchool(),
            model.getCourse()
        )
    }

    override fun handleSignOut() {
        view.navigateToLogin()
    }
}