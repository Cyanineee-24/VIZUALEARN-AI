package com.myApp.vizualearnfinal.screens.editprofile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.utils.*

class EditProfileActivity : AppCompatActivity(), EditProfileContract.View {

    private lateinit var presenter: EditProfileContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val app = application as CustomApplication
        presenter = EditProfilePresenter(this, EditProfileModel(app))

        // 1. Fill the boxes with the current data
        presenter.loadCurrentUserData()

        // 2. Handle Save
        getTextView(R.id.textviewSaveChangesBtn)?.setOnClickListener {
            presenter.saveChanges(
                first = getEditTextStringValue(R.id.edittextFirstName),
                last = getEditTextStringValue(R.id.edittextLastName),
                email = getEditTextStringValue(R.id.edittextEmailAddress),
                school = getEditTextStringValue(R.id.edittextSchoolUniversity),
                course = getEditTextStringValue(R.id.edittextFieldOfStudy),
                address = getEditTextStringValue(R.id.edittextAddress)
            )
        }

        // 3. Handle Cancels/Backs
        getTextView(R.id.textviewCancelLink)?.setOnClickListener { navigateBackToProfile() }
        getImageView(R.id.imageviewBack)?.setOnClickListener { navigateBackToProfile() }
    }

    override fun populateUserData(first: String, last: String, email: String, school: String, course: String, address: String) {
        getEditText(R.id.edittextFirstName)?.setText(first)
        getEditText(R.id.edittextLastName)?.setText(last)
        getEditText(R.id.edittextEmailAddress)?.setText(email)
        getEditText(R.id.edittextSchoolUniversity)?.setText(school)
        getEditText(R.id.edittextFieldOfStudy)?.setText(course)
        getEditText(R.id.edittextAddress)?.setText(address)
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun navigateBackToProfile() {
        finish() // Pops this screen off, revealing the Profile screen underneath!
    }
}