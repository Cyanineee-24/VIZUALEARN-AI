package com.myApp.vizualearnfinal.screens.step3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.step4.Step4Activity
import com.myApp.vizualearnfinal.utils.*

class Step3Activity : AppCompatActivity(), Step3Contract.View {

    private lateinit var presenter: Step3Contract.Presenter
    private var setId: Int = 0
    private var creationType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step3_generating)

        // 1. Catch the data passed from Step 2
        setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"

        // NEW: Catch the notes passed from Step 2!
        val notes = intent.getStringExtra("EXTRA_NOTES") ?: "No notes provided"

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = Step3Presenter(this, Step3Model(repository))

        // Update the UI dynamically for Mind Map vs Flashcards
        setupDynamicUI()

        // Fetch banner details
        presenter.loadStudySetDetails(setId)

        // 2. UPDATED: Pass the notes and the type into the simulation!
        presenter.startGeneratingSimulation(notes, creationType)

        // This will be un-clickable until the Presenter enables it
        getTextView(R.id.textviewContinue)?.setOnClickListener {
            presenter.onContinueClicked(setId, creationType)
        }

        getImageView(R.id.imageviewBack)?.setOnClickListener { finish() }
    }
    private fun setupDynamicUI() {
        if (creationType == "mindmap") {
            // Setup Mind Map visual variant
            getImageView(R.id.imageviewGeneratingIcon)?.setImageResource(R.drawable.ic_generating_brain) // Use your brain icon
            getImageView(R.id.imageviewGeneratingIcon)?.setBackgroundResource(R.drawable.bg_circle_light_purple)
            getTextView(R.id.textviewGeneratingTitle)?.text = "Generating your mind map...."
            getTextView(R.id.textviewGeneratingSubtitle)?.text = "Gemini is reading your notes and plotting your mind map"
        } else {
            // Setup Flashcard visual variant
            getImageView(R.id.imageviewGeneratingIcon)?.setImageResource(R.drawable.ic_flashcards) // Use your flashcard icon
            getImageView(R.id.imageviewGeneratingIcon)?.setBackgroundResource(R.drawable.bg_circle_light_red)
            getTextView(R.id.textviewGeneratingTitle)?.text = "Generating your flash cards...."
            getTextView(R.id.textviewGeneratingSubtitle)?.text = "Gemini is reading your notes and crafting your flash cards"
        }
    }

    // --- Contract View Methods ---

    override fun displaySavingLocation(setName: String) {
        getTextView(R.id.textviewSavingInto)?.text = "Saving into: $setName"
    }

    override fun enableContinueButton() {
        // Change the background to your active orange button drawable and make it clickable
        val btnContinue = getTextView(R.id.textviewContinue)
        btnContinue?.setBackgroundResource(R.drawable.bg_btn_continue_orange)
        btnContinue?.isClickable = true
    }

    // Step3Activity.kt — navigateToStep4()
    override fun navigateToStep4(setId: Int, type: String, jsonResult: String) {
        val intent = Intent(this, Step4Activity::class.java).apply {
            putExtra("EXTRA_SET_ID", setId)
            putExtra("EXTRA_TYPE", type)
            putExtra("EXTRA_JSON_RESULT", jsonResult)
            // ✅ Use this@Step3Activity.intent to read the INCOMING intent
            putExtra("EXTRA_ITEM_NAME", this@Step3Activity.intent.getStringExtra("EXTRA_ITEM_NAME") ?: "Untitled")
        }
        startActivity(intent)
        finish()
    }
}