package com.myApp.vizualearnfinal.screens.step1

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.step2.Step2Activity
import com.myApp.vizualearnfinal.utils.*

class Step1Activity : AppCompatActivity(), Step1Contract.View {

    private lateinit var presenter: Step1Contract.Presenter
    private var setId: Int = 0
    private var creationType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step1_input)

        setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"

        // Initialize MVP
        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = Step1Presenter(this, Step1Model(repository))

        // Fetch data
        presenter.loadStudySetDetails(setId)

        // Delegate Option Clicks to Presenter
        getRelativeLayout(R.id.relativelayoutOptionImage)?.setOnClickListener { presenter.onInputMethodSelected("IMAGE") }
        getRelativeLayout(R.id.relativelayoutOptionPdf)?.setOnClickListener { presenter.onInputMethodSelected("PDF") }
        getRelativeLayout(R.id.relativelayoutOptionText)?.setOnClickListener { presenter.onInputMethodSelected("TEXT") }

        // Delegate Continue Click to Presenter
        getTextView(R.id.textviewContinue)?.setOnClickListener {
            presenter.onContinueClicked(setId, creationType)
        }

        getImageView(R.id.imageviewBack)?.setOnClickListener { finish() }
    }

    // --- Contract View Methods ---

    override fun displaySavingLocation(setName: String) {
        getTextView(R.id.textviewSavingInto)?.text = "Saving into: $setName"
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun updateRadioSelection(selectedMethod: String) {
        // Reset all first
        findViewById<RadioButton>(R.id.radiobuttonImage)?.isChecked = false
        findViewById<RadioButton>(R.id.radiobuttonPdf)?.isChecked = false
        findViewById<RadioButton>(R.id.radiobuttonText)?.isChecked = false

        // Check the correct one
        when (selectedMethod) {
            "IMAGE" -> findViewById<RadioButton>(R.id.radiobuttonImage)?.isChecked = true
            "PDF" -> findViewById<RadioButton>(R.id.radiobuttonPdf)?.isChecked = true
            "TEXT" -> findViewById<RadioButton>(R.id.radiobuttonText)?.isChecked = true
        }
    }

    override fun navigateToStep2(setId: Int, type: String, inputMethod: String) {
        val intent = Intent(this, Step2Activity::class.java).apply {
            putExtra("EXTRA_SET_ID", setId)
            putExtra("EXTRA_TYPE", type)
            putExtra("EXTRA_INPUT_METHOD", inputMethod)
        }
        startActivity(intent)
    }
}