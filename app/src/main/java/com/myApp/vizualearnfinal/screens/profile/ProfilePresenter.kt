package com.myApp.vizualearnfinal.screens.profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfilePresenter(
    private val view: ProfileContract.View,
    private val model: ProfileModel
) : ProfileContract.Presenter {

    override fun loadProfileData() {
        val formattedUsername = "@${model.getUsername()}"

        // Pass the address to the view!
        view.displayUserProfile(
            formattedUsername,
            model.getEmail(),
            model.getSchool(),
            model.getCourse(),
            model.getAddress(),
            model.getMemberSince() // <-- NEW
        )

        // Fetch the real stats from Room Database in the background
        CoroutineScope(Dispatchers.Main).launch {
            val (mindMaps, cards) = model.getDatabaseStats()
            view.displayStats(mindMaps, cards)
        }
    }

    override fun handleSignOut() {
        model.clearSession()
        view.navigateToLogin()
    }
}