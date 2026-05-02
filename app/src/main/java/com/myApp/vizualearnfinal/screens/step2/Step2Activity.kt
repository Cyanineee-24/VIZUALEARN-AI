package com.myApp.vizualearnfinal.screens.step2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.step3.Step3Activity
import com.myApp.vizualearnfinal.utils.*

class Step2Activity : AppCompatActivity(), Step2Contract.View {

    private lateinit var presenter: Step2Contract.Presenter
    private var setId: Int = 0
    private var creationType: String = ""
    private var inputMethod: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step2_review)

        setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"
        inputMethod = intent.getStringExtra("EXTRA_INPUT_METHOD") ?: "TEXT"

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = Step2Presenter(this, Step2Model(repository))

        presenter.loadStudySetDetails(setId)

        setupDynamicUI()

        getLinearLayout(R.id.linearlayoutGenerateButton)?.setOnClickListener {
            val itemName = getEditTextStringValue(R.id.edittextName)
            presenter.validateAndGenerate(itemName)
        }

        getImageView(R.id.imageviewBack)?.setOnClickListener { finish() }
    }

    private fun setupDynamicUI() {
        // Adjust text for Mind Map vs Flashcards
        if (creationType == "mindmap") {
            getTextView(R.id.textviewDynamicTitle)?.text = "Name your Mind Map"
            getTextView(R.id.textviewGenerateButtonText)?.text = "Generate Mind Map"
        } else {
            getTextView(R.id.textviewDynamicTitle)?.text = "Name your Flash Cards"
            getTextView(R.id.textviewGenerateButtonText)?.text = "Generate Flash Cards"
        }

        // Setup the specific variant (Text, Image, PDF)
        val textVariant = getEditText(R.id.edittextExtractedText)
        val imageVariant = getLinearLayout(R.id.linearlayoutUploadImage)
        val pdfVariant = getLinearLayout(R.id.linearlayoutUploadPdf)
        val variantTitle = getTextView(R.id.textviewVariantTitle)

        // Hide all first
        textVariant?.visibility = View.GONE
        imageVariant?.visibility = View.GONE
        pdfVariant?.visibility = View.GONE

        // Show the correct one
        when (inputMethod) {
            "IMAGE" -> {
                imageVariant?.visibility = View.VISIBLE
                variantTitle?.text = "Upload Image"
            }
            "PDF" -> {
                pdfVariant?.visibility = View.VISIBLE
                variantTitle?.text = "Upload PDF"
            }
            "TEXT" -> {
                textVariant?.visibility = View.VISIBLE
                variantTitle?.text = "Extracted Text – review before generating"
            }
        }
    }

    override fun displaySavingLocation(setName: String) {
        getTextView(R.id.textviewSavingInto)?.text = "Saving into: $setName"
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun navigateToStep3() {
        val intent = Intent(this, Step3Activity::class.java).apply {
            putExtra("EXTRA_SET_ID", setId)
            putExtra("EXTRA_TYPE", creationType)

            // ADD THESE TWO LINES: Pass the data forward!
            putExtra("EXTRA_ITEM_NAME", getEditTextStringValue(R.id.edittextName))
            putExtra("EXTRA_NOTES", getEditTextStringValue(R.id.edittextExtractedText))
        }
        startActivity(intent)
    }
}