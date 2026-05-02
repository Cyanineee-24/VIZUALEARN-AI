package com.myApp.vizualearnfinal.screens.register

import android.app.Activity
import android.os.Bundle
import android.widget.CheckBox
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.utils.getButtonView
import com.myApp.vizualearnfinal.utils.getEditTextStringValue
import com.myApp.vizualearnfinal.utils.getImageView
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.toast

class RegisterActivity : Activity(), RegisterContract.View {

    private lateinit var presenter: RegisterPresenter
    private lateinit var app: CustomApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        app = application as CustomApplication
        presenter = RegisterPresenter(this, RegisterModel(app))

        // Handle the values when "Create Account" button is clicked
        getButtonView(R.id.buttonCreateAccount)?.setOnClickListener {
            val first = getEditTextStringValue(R.id.edittextFirstName)
            val last = getEditTextStringValue(R.id.edittextLastName)
            val email = getEditTextStringValue(R.id.edittextEmail)
            val school = getEditTextStringValue(R.id.edittextSchool)
            val course = getEditTextStringValue(R.id.edittextFieldOfStudy)
            val pass = getEditTextStringValue(R.id.edittextPassword)
            val confirm = getEditTextStringValue(R.id.edittextConfirmPassword)
            val username = getEditTextStringValue(R.id.edittextUsername)

            // Get checkbox state
            val termsChecked = findViewById<CheckBox>(R.id.checkboxTerms).isChecked

            presenter.register(username, first, last, email, school, course, pass, confirm, termsChecked)
        }

        // If user presses the back to login
        getTextView(R.id.textviewBackToLogin)?.setOnClickListener {
            navigateBackToLogin()
        }

        // If user has an account, and pressed "Sign in"
        getTextView(R.id.textViewSignIn)?.setOnClickListener {
            navigateBackToLogin()
        }

        getImageView(R.id.imageviewBackToLoginSymbol)?.setOnClickListener {
            navigateBackToLogin()
        }
    }

    override fun showSuccessMessage() {
        toast("Account created successfully!")
    }

    override fun showEmptyFieldsMessage() {
        toast("Please fill out all fields.")
    }

    override fun showPasswordMismatchMessage() {
        toast("Passwords do not match!")
    }

    override fun showTermsNotAcceptedMessage() {
        toast("You must agree to the Terms of Service.")
    }

    override fun navigateBackToLogin() {
        finish()
    }
}