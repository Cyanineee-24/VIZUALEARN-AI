package com.myApp.vizualearnfinal.screens.step4

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.dashboard.DashboardActivity
import com.myApp.vizualearnfinal.utils.*

class Step4Activity : AppCompatActivity(), Step4Contract.View {

    private lateinit var presenter: Step4Contract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step4_result)

        val setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        val creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"

        // NEW: Catch the JSON!
        val jsonResult = intent.getStringExtra("EXTRA_JSON_RESULT") ?: "[]"

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = Step4Presenter(this, Step4Model(repository))

        // NEW: Pass the JSON to the presenter
        presenter.initializeView(setId, creationType, jsonResult)

        getTextView(R.id.textviewSave)?.setOnClickListener {
            presenter.onSaveClicked()
        }
        getImageView(R.id.imageviewBack)?.setOnClickListener {
            finish()
        }
    }

    override fun showFlashcardsUI(setName: String, cards: List<Flashcard>) {
        getLinearLayout(R.id.linearlayoutMindMapVariant)?.visibility = View.GONE
        getLinearLayout(R.id.linearlayoutFlashcardsVariant)?.visibility = View.VISIBLE

        getTextView(R.id.textviewFlashcardSetName)?.text = setName
        getTextView(R.id.textviewCardCountBadge)?.text = "${cards.size} Cards"

        // Dynamically inflate the cards!
        // Make sure you add an ID called 'linearlayoutDynamicCards' to your XML!
        val container = getLinearLayout(R.id.linearlayoutDynamicCards)
        container?.removeAllViews()

        val inflater = LayoutInflater.from(this)
        for ((index, card) in cards.withIndex()) {
            val itemView = inflater.inflate(R.layout.item_flashcard_preview, container, false)

            itemView.getTextView(R.id.textviewBadgeNumber)?.text = "${index + 1}"
            itemView.getTextView(R.id.textviewCardTitle)?.text = "CARD ${index + 1}"
            itemView.getTextView(R.id.textviewCardFront)?.text = card.frontText
            itemView.getTextView(R.id.textviewCardBack)?.text = card.backText

            container?.addView(itemView)
        }
    }

    override fun showMindMapUI(setName: String, nodes: List<MindMapNode>) {
        getLinearLayout(R.id.linearlayoutFlashcardsVariant)?.visibility = View.GONE
        getLinearLayout(R.id.linearlayoutMindMapVariant)?.visibility = View.VISIBLE

        // We will build the dynamic list logic for Mind Maps in Phase 4
        // when we tackle the visual canvas!
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun finishToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}