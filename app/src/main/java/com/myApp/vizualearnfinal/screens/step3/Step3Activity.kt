package com.myApp.vizualearnfinal.screens.step3

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.step4.Step4Activity

class Step3Activity : AppCompatActivity(), Step3Contract.View {

    private lateinit var presenter: Step3Contract.Presenter
    private var setId: Int = 0
    private var creationType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step3_generating)

        setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"
        val notes = intent.getStringExtra("EXTRA_NOTES") ?: ""
        val inputMethod = intent.getStringExtra("EXTRA_INPUT_METHOD") ?: "TEXT"
        val imageUris = intent.getStringArrayListExtra("EXTRA_IMAGE_URIS") ?: ArrayList()
        val pdfUri = intent.getStringExtra("EXTRA_PDF_URI")

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)

        presenter = Step3Presenter(this, Step3Model(this, repository))

        setupDynamicUI()
        presenter.loadStudySetDetails(setId)

        presenter.startGeneratingSimulation(notes, creationType, inputMethod, imageUris, pdfUri)

        findViewById<TextView>(R.id.textviewContinue)?.setOnClickListener {
            presenter.onContinueClicked(setId, creationType)
        }
        findViewById<ImageView>(R.id.imageviewBack)?.setOnClickListener { finish() }
    }

    private fun setupDynamicUI() {
        val icon = findViewById<ImageView>(R.id.imageviewGeneratingIcon)
        val title = findViewById<TextView>(R.id.textviewGeneratingTitle)
        val subtitle = findViewById<TextView>(R.id.textviewGeneratingSubtitle)

        if (creationType == "mindmap") {
            icon?.setImageResource(R.drawable.ic_generating_brain)
            icon?.setBackgroundResource(R.drawable.bg_circle_light_purple)
            title?.text = "Generating your mind map...."
            subtitle?.text = "Gemini is reading your files and plotting your mind map"
        } else {
            icon?.setImageResource(R.drawable.ic_flashcards)
            icon?.setBackgroundResource(R.drawable.bg_circle_light_red)
            title?.text = "Generating your flash cards...."
            subtitle?.text = "Gemini is reading your files and crafting your flash cards"
        }
    }

    override fun displaySavingLocation(setName: String) {
        findViewById<TextView>(R.id.textviewSavingInto)?.text = "Saving into: $setName"
    }

    override fun enableContinueButton() {
        val btnContinue = findViewById<TextView>(R.id.textviewContinue)
        btnContinue?.setBackgroundResource(R.drawable.bg_btn_continue_orange)
        btnContinue?.isClickable = true
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToStep4(setId: Int, type: String, jsonResult: String) {
        val intent = Intent(this, Step4Activity::class.java).apply {
            putExtra("EXTRA_SET_ID", setId)
            putExtra("EXTRA_TYPE", type)
            putExtra("EXTRA_JSON_RESULT", jsonResult)
            putExtra("EXTRA_ITEM_NAME", this@Step3Activity.intent.getStringExtra("EXTRA_ITEM_NAME") ?: "Untitled")
        }
        startActivity(intent)
        finish()
    }
}