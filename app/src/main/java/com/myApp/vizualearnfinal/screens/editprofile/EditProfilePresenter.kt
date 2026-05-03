package com.myApp.vizualearnfinal.screens.editprofile

class EditProfilePresenter(
    private val view: EditProfileContract.View,
    private val model: EditProfileModel
) : EditProfileContract.Presenter {

    override fun loadCurrentData() {
        val data = model.getCurrentData()
        view.populateFields(
            first = data["FIRST"] ?: "",
            last = data["LAST"] ?: "",
            email = data["EMAIL"] ?: "",
            school = data["SCHOOL"] ?: "",
            course = data["COURSE"] ?: "",
            address = data["ADDRESS"] ?: ""
        )
    }

    override fun saveChanges(first: String, last: String, email: String, school: String, course: String, address: String) {
        if (first.isEmpty() || email.isEmpty()) {
            view.showMessage("First Name and Email are required.")
            return
        }

        model.saveChanges(first, last, email, school, course, address)
        view.showMessage("Profile updated successfully!")
        view.finishActivity()
    }
}