package com.myApp.vizualearnfinal.screens.register

class RegisterPresenter(
    val view: RegisterContract.View,
    val model: RegisterModel
) : RegisterContract.Presenter {

    override fun register(
        username: String,
        first: String,
        last: String,
        email: String,
        address: String,
        school: String,
        course: String,
        pass: String,
        confirmPass: String,
        isTermsChecked: Boolean
    ) {
        // 1. Check if ANY field is empty
        if (first.isEmpty() || last.isEmpty() || email.isEmpty() ||
            school.isEmpty() || course.isEmpty() || pass.isEmpty()) {
            view.showEmptyFieldsMessage()
            return
        }

        // 2. Check if passwords match
        if (pass != confirmPass) {
            view.showPasswordMismatchMessage()
            return
        }

        // 3. Check if Terms are accepted
        if (!isTermsChecked) {
            view.showTermsNotAcceptedMessage()
            return
        }

        // 4. If everything is perfect, save them
        model.saveNewUser(username, first, last, email, school, course, pass)
        view.showSuccessMessage()
        view.navigateBackToLogin()
    }
}