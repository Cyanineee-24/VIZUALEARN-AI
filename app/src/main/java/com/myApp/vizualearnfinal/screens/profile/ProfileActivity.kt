package com.myApp.vizualearnfinal.screens.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.editprofile.EditProfileActivity
import com.myApp.vizualearnfinal.screens.login.LoginActivity
import com.myApp.vizualearnfinal.utils.getImageView
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.setTextViewStringValue


class ProfileActivity : AppCompatActivity(), ProfileContract.View {
    private lateinit var presenter: ProfilePresenter
    private lateinit var app: CustomApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        app = application as CustomApplication

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)

        presenter = ProfilePresenter(this, ProfileModel(this, app, repository))


        getImageView(R.id.imageviewEdit)?.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        findViewById<ImageView>(R.id.imageviewBack)?.setOnClickListener { navigateBack() }
        getTextView(R.id.buttonSignOut)?.setOnClickListener { presenter.handleSignOut() }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadProfileData()
    }

    override fun displayUserProfile(username: String, email: String, school: String, course: String, address: String, memberSince: String) {
        setTextViewStringValue(R.id.textviewName, username)
        setTextViewStringValue(R.id.textviewEmail, email)
        setTextViewStringValue(R.id.textviewSchool, school)
        setTextViewStringValue(R.id.textviewSchool2, school)
        setTextViewStringValue(R.id.textviewCourse, course)
        setTextViewStringValue(R.id.textViewLocation, address)
        setTextViewStringValue(R.id.textviewMemberSince, memberSince)
    }

    override fun displayStats(mindMapsCount: Int, cardsCount: Int) {
        setTextViewStringValue(R.id.textviewMindMapsCount, mindMapsCount.toString())
        setTextViewStringValue(R.id.textviewCardsCount, cardsCount.toString())
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