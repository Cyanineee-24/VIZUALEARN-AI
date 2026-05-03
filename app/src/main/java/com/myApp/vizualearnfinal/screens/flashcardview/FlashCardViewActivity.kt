package com.myApp.vizualearnfinal.screens.flashcardview

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.toast

class FlashCardViewActivity : AppCompatActivity(), FlashCardViewContract.View {

    private lateinit var presenter: FlashCardViewContract.Presenter

    // UI References
    private lateinit var linearlayoutFlashcardContainer: LinearLayout
    private lateinit var textviewCardType: TextView
    private lateinit var textviewCardContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card_view)

        val deckId = intent.getIntExtra("EXTRA_DECK_ID", -1)

        val dao = AppDatabase.getDatabase(this).studySetDao()
        presenter = FlashCardViewPresenter(this, FlashCardViewModel(StudySetRepository(dao)))

        // Initialize UI Elements
        linearlayoutFlashcardContainer = findViewById(R.id.linearlayoutFlashcardContainer)
        textviewCardType = findViewById(R.id.textviewCardType)
        textviewCardContent = findViewById(R.id.textviewCardContent)

        // Header Actions
        findViewById<ImageView>(R.id.imageviewBack).setOnClickListener { finishActivity() }

        // Card Actions
        linearlayoutFlashcardContainer.setOnClickListener { presenter.onCardTapped() }
        findViewById<ImageView>(R.id.imageviewPrevCard).setOnClickListener { presenter.onPrevClicked() }
        findViewById<ImageView>(R.id.imageviewNextCard).setOnClickListener { presenter.onNextClicked() }

        // Scoring Actions
        findViewById<TextView>(R.id.textviewSkip).setOnClickListener { presenter.onActionClicked("SKIP") }
        findViewById<TextView>(R.id.textviewWrong).setOnClickListener { presenter.onActionClicked("WRONG") }
        findViewById<TextView>(R.id.textviewCorrect).setOnClickListener { presenter.onActionClicked("CORRECT") }

        // Study Modes
        findViewById<LinearLayout>(R.id.linearlayoutStudyMode).setOnClickListener { presenter.onModeSelected("Study Mode") }
        findViewById<LinearLayout>(R.id.linearlayoutQuizMode).setOnClickListener { presenter.onModeSelected("Quiz Mode++") }
        findViewById<LinearLayout>(R.id.linearlayoutReviewMissed).setOnClickListener { presenter.onModeSelected("Review Missed") }

        // Edit Card
        findViewById<TextView>(R.id.textviewEditThisCard).setOnClickListener { presenter.onEditCardClicked() }

        // Load data
        presenter.loadDeck(deckId)
    }

    override fun setHeaders(deckTitle: String, parentSetTitle: String, totalCards: Int) {
        findViewById<TextView>(R.id.textviewDeckTitle).text = deckTitle
        findViewById<TextView>(R.id.textviewParentSetName).text = parentSetTitle
        findViewById<TextView>(R.id.textviewTotalCardsBadge).text = "$totalCards Cards"
    }

    override fun displayCard(card: Flashcard, isShowingFront: Boolean, currentIndex: Int, totalCards: Int) {
        if (isShowingFront) {
            linearlayoutFlashcardContainer.setBackgroundResource(R.drawable.bg_box_q_red)
            textviewCardType.text = "QUESTION"
            textviewCardContent.text = card.frontText
        } else {
            linearlayoutFlashcardContainer.setBackgroundResource(R.drawable.bg_box_a_green)
            textviewCardType.text = "ANSWER"

            // If the card has extra context, append it to the back of the card
            if (!card.contextText.isNullOrEmpty()) {
                textviewCardContent.text = "${card.backText}\n\nContext:\n${card.contextText}"
            } else {
                textviewCardContent.text = card.backText
            }
        }
    }

    override fun updateProgressUI(learnedCount: Int, totalCards: Int) {
        val progressBar = findViewById<ProgressBar>(R.id.progressbarStudy)
        val progressText = findViewById<TextView>(R.id.textviewProgressText)

        progressBar.max = totalCards
        progressBar.progress = learnedCount
        progressText.text = "$learnedCount of $totalCards learned"
    }

    override fun showEditCardUI(card: Flashcard) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_edit_card, null)
        bottomSheetDialog.setContentView(view)

        // Find the views inside the bottom sheet
        val edittextEditFront = view.findViewById<EditText>(R.id.edittextEditFront)
        val edittextEditBack = view.findViewById<EditText>(R.id.edittextEditBack)
        val edittextEditContext = view.findViewById<EditText>(R.id.edittextEditContext)
        val textviewSaveCardChanges = view.findViewById<TextView>(R.id.textviewSaveCardChanges)

        // WIRED TO YOUR NEW CUSTOM CANCEL BUTTON
        val textviewCancel = view.findViewById<TextView>(R.id.textviewCancel)

        // Pre-fill the current card data
        edittextEditFront.setText(card.frontText)
        edittextEditBack.setText(card.backText)
        edittextEditContext.setText(card.contextText ?: "")

        // Handle Cancel button
        textviewCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Handle Save button
        textviewSaveCardChanges.setOnClickListener {
            val newFront = edittextEditFront.text.toString().trim()
            val newBack = edittextEditBack.text.toString().trim()
            val newContext = edittextEditContext.text.toString().trim().takeIf { it.isNotEmpty() }

            if (newFront.isEmpty() || newBack.isEmpty()) {
                toast("Front and Back text cannot be empty!")
                return@setOnClickListener
            }

            presenter.saveEditedCard(card.id, newFront, newBack, newContext)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun finishActivity() {
        finish()
    }
}