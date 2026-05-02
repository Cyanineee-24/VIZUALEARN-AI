package com.myApp.vizualearnfinal.screens.editprofile

class EditProfilePresenter(
    private val view: EditProfileContract.View,
    private val model: EditProfileModel
) : EditProfileContract.Presenter {

    override fun loadCurrentUserData() {
        val user = model.getCurrentUser()
        if (user != null) {
            view.populateUserData(
                first = user.firstName,
                last = user.lastName,
                email = user.email,
                school = user.school,
                course = user.course,
                address = "" // Pass address here once you add it to the User data class
            )
        }
    }

    override fun saveChanges(first: String, last: String, email: String, school: String, course: String, address: String) {
        // Basic validation
        if (first.isBlank() || last.isBlank() || email.isBlank()) {
            view.showMessage("First name, last name, and email cannot be empty.")
            return
        }

        model.updateCurrentUser(first, last, email, school, course, address)
        view.showMessage("Profile updated successfully!")
        view.navigateBackToProfile()
    }
}