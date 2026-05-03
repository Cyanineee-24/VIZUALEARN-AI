package com.myApp.vizualearnfinal.screens.flashcardview

import android.content.Intent
import android.graphics.Color
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
import com.myApp.vizualearnfinal.screens.editdeck.EditDeckActivity
import com.myApp.vizualearnfinal.utils.toast

class FlashCardViewActivity : AppCompatActivity(), FlashCardViewContract.View {

    private lateinit var presenter: FlashCardViewContract.Presenter

    // UI References
    private lateinit var linearlayoutFlashcardContainer: LinearLayout
    private lateinit var textviewCardType: TextView
    private lateinit var textviewCardContent: TextView

    // Track the deck ID for when we return from the Edit screen
    private var currentDeckId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card_view)

        currentDeckId = intent.getIntExtra("EXTRA_DECK_ID", -1)

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

        // Open the new Bulk Edit/Review Screen
        findViewById<LinearLayout>(R.id.linearlayoutReviewEditMode)?.setOnClickListener {
            presenter.onReviewEditModeClicked()
        }

        findViewById<LinearLayout>(R.id.linearlayoutQuizMode).setOnClickListener {
            presenter.onModeSelected("Quiz Mode++")
        }

        // Edit Card
        findViewById<TextView>(R.id.textviewEditThisCard).setOnClickListener {
            presenter.onEditCardClicked()
        }
    }

    // Refresh the data every time the screen becomes visible (e.g., coming back from Edit screen)
    override fun onResume() {
        super.onResume()
        presenter.loadDeck(currentDeckId)
    }

    override fun setHeaders(deckTitle: String, parentSetTitle: String, totalCards: Int) {
        findViewById<TextView>(R.id.textviewDeckTitle).text = deckTitle
        findViewById<TextView>(R.id.textviewParentSetName).text = parentSetTitle
        findViewById<TextView>(R.id.textviewTotalCardsBadge).text = "$totalCards Cards"
    }

    override fun displayCard(card: Flashcard, isShowingFront: Boolean, currentIndex: Int, totalCards: Int) {
        if (isShowingFront) {
            linearlayoutFlashcardContainer.setBackgroundResource(R.drawable.bg_flashcard_question)
            textviewCardType.text = "QUESTION"
            textviewCardType.setTextColor(Color.parseColor("#FFD700")) // gold label
            textviewCardContent.text = card.frontText
        } else {
            linearlayoutFlashcardContainer.setBackgroundResource(R.drawable.bg_flashcard_answer)
            textviewCardType.text = "ANSWER"
            textviewCardType.setTextColor(Color.parseColor("#A5D6A7")) // light green label
            textviewCardContent.text = if (!card.contextText.isNullOrEmpty()) {
                "${card.backText}\n\nContext:\n${card.contextText}"
            } else {
                card.backText
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

    override fun navigateToEditDeck(deckId: Int) {
        val intent = Intent(this, EditDeckActivity::class.java)
        intent.putExtra("EXTRA_DECK_ID", deckId)
        startActivity(intent)
    }
}