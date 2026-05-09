package com.myApp.vizualearnfinal.screens.login

import android.app.Activity
import android.os.Bundle
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.screens.dashboard.DashboardActivity
import com.myApp.vizualearnfinal.screens.forgotpassword.ForgotPasswordActivity
import com.myApp.vizualearnfinal.screens.register.RegisterActivity
import com.myApp.vizualearnfinal.utils.getButtonView
import com.myApp.vizualearnfinal.utils.getEditTextStringValue
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.start
import com.myApp.vizualearnfinal.utils.toast

class LoginActivity : Activity(), LoginContract.View {

    private lateinit var loginPresenter: LoginPresenter
    private lateinit var app: CustomApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        app = application as CustomApplication
        loginPresenter = LoginPresenter(this, LoginModel(app))

        getButtonView(R.id.buttonSignIn)?.setOnClickListener {
            val username = getEditTextStringValue(R.id.edittextUsername)
            val password = getEditTextStringValue(R.id.edittextPassword)
            loginPresenter.login(username, password)
        }

        getTextView(R.id.textviewCreateAccount)?.setOnClickListener {
            loginPresenter.createAccount()
        }

        getTextView(R.id.textviewForgotPassword)?.setOnClickListener {
            loginPresenter.forgotPassword()
        }
    }

    override fun showSuccessMessage() {
        toast("Login successful, welcome aboard ${app.loginUser?.username ?: ""}!")
    }

    override fun showInvalidCredentialsMessage() {
        toast("Invalid Credentials. Try again!")
    }

    override fun showEmptyFieldsMessage() {
        toast("Fields cannot be empty!")
    }

    override fun navigateToRegisterScreen() {
        start(RegisterActivity::class.java)
    }

    override fun navigateToDashboardScreen() {
        start(DashboardActivity::class.java)
    }

    override fun navigateToForgotPasswordScreen() {
        start(ForgotPasswordActivity::class.java)
    }
}