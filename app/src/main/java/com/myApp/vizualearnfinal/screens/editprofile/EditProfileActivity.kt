package com.myApp.vizualearnfinal.screens.editprofile

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.utils.getEditTextStringValue
import com.myApp.vizualearnfinal.utils.toast

class EditProfileActivity : AppCompatActivity(), EditProfileContract.View {

    private lateinit var presenter: EditProfileContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        presenter = EditProfilePresenter(this, EditProfileModel(this))

        // Back and Cancel Buttons
        findViewById<ImageView>(R.id.imageviewBack)?.setOnClickListener { finishActivity() }
        findViewById<TextView>(R.id.textviewCancelLink)?.setOnClickListener { finishActivity() }

        // Save Button
        findViewById<TextView>(R.id.textviewSaveChangesBtn)?.setOnClickListener {
            val first = getEditTextStringValue(R.id.edittextFirstName)
            val last = getEditTextStringValue(R.id.edittextLastName)
            val email = getEditTextStringValue(R.id.edittextEmailAddress)
            val school = getEditTextStringValue(R.id.edittextSchoolUniversity)
            val course = getEditTextStringValue(R.id.edittextFieldOfStudy)
            val address = getEditTextStringValue(R.id.edittextAddress)

            presenter.saveChanges(first, last, email, school, course, address)
        }

        // Load current data instantly
        presenter.loadCurrentData()
    }

    override fun populateFields(first: String, last: String, email: String, school: String, course: String, address: String) {
        findViewById<EditText>(R.id.edittextFirstName)?.setText(first)
        findViewById<EditText>(R.id.edittextLastName)?.setText(last)
        findViewById<EditText>(R.id.edittextEmailAddress)?.setText(email)
        findViewById<EditText>(R.id.edittextSchoolUniversity)?.setText(school)
        findViewById<EditText>(R.id.edittextFieldOfStudy)?.setText(course)
        findViewById<EditText>(R.id.edittextAddress)?.setText(address)
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun finishActivity() {
        finish()
    }
}