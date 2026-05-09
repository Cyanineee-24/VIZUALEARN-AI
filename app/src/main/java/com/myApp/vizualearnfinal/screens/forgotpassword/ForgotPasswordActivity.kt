package com.myApp.vizualearnfinal.screens.forgotpassword

import android.app.Activity
import android.os.Bundle
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.utils.getButtonView
import com.myApp.vizualearnfinal.utils.getEditTextStringValue
import com.myApp.vizualearnfinal.utils.getImageView
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.toast

class ForgotPasswordActivity : Activity(), ForgotPasswordContract.View {

    private lateinit var presenter: ForgotPasswordPresenter
    private lateinit var app: CustomApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        app = application as CustomApplication
        presenter = ForgotPasswordPresenter(this, ForgotPasswordModel(app), app)

        getButtonView(R.id.buttonConfirmChanges)?.setOnClickListener {
            val username = getEditTextStringValue(R.id.edittextUsername)
            val newPass = getEditTextStringValue(R.id.edittextNewPassword)
            val confirmPass = getEditTextStringValue(R.id.edittextConfirmNewPassword)
            
            presenter.updatePassword(username, newPass, confirmPass)
        }

        getTextView(R.id.textviewBackToLogin)?.setOnClickListener {
            navigateBackToLogin()
        }

        getImageView(R.id.imageviewBackToLoginSymbol)?.setOnClickListener {
            navigateBackToLogin()
        }
    }

    override fun showSuccessMessage() {
        toast("Password updated successfully!")
    }

    override fun showUserNotFoundMessage() {
        toast("User not found!")
    }

    override fun showPasswordMismatchMessage() {
        toast("Passwords do not match!")
    }

    override fun showEmptyFieldsMessage() {
        toast("Please fill out all fields.")
    }

    override fun navigateBackToLogin() {
        finish()
    }
}