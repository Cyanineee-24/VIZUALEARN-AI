package com.myApp.vizualearnfinal.screens.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity // Updated to AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.screens.editprofile.EditProfileActivity
import com.myApp.vizualearnfinal.screens.login.LoginActivity
import com.myApp.vizualearnfinal.utils.getImageView
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.setTextViewStringValue
import com.myApp.vizualearnfinal.utils.setupUniversalFooter

class ProfileActivity : AppCompatActivity(), ProfileContract.View { // Ensure it extends AppCompatActivity

    private lateinit var presenter: ProfilePresenter
    private lateinit var app: CustomApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. ALWAYS set the content view first!
        setContentView(R.layout.activity_profile)

        // Initialize App and Presenter safely
        app = application as CustomApplication
        presenter = ProfilePresenter(this, ProfileModel(app))

        // 2. NOW you can wire up the footer
        setupUniversalFooter()

        // 3. NEW: Wire up the Edit Profile button!
        getImageView(R.id.imageviewEdit)?.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Handle Back Arrow Click
        findViewById<ImageView>(R.id.imageviewBack)?.setOnClickListener {
            navigateBack()
        }

        // Handle Sign Out Click
        getTextView(R.id.buttonSignOut)?.setOnClickListener {
            presenter.handleSignOut()
        }
    }

    override fun onResume() {
        super.onResume()
        // This is perfectly safe because onCreate always finishes before onResume starts!
        presenter.loadProfileData()
    }

    override fun displayUserProfile(username: String, email: String, school: String, course: String) {
        setTextViewStringValue(R.id.textviewName, username)
        setTextViewStringValue(R.id.textviewEmail, email)
        setTextViewStringValue(R.id.textviewSchool, school)
        setTextViewStringValue(R.id.textviewSchool2, school)
        setTextViewStringValue(R.id.textviewCourse, course)
    }

    override fun navigateBack() {
        finish()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}