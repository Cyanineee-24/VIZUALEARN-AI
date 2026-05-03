package com.myApp.vizualearnfinal.screens.editdeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.toast

class EditDeckActivity : AppCompatActivity(), EditDeckContract.View {

    private lateinit var presenter: EditDeckContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // REUSING THE STEP 4 RESULT LAYOUT
        setContentView(R.layout.activity_step4_result)

        val deckId = intent.getIntExtra("EXTRA_DECK_ID", -1)

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = EditDeckPresenter(this, EditDeckModel(repository))

        setupUI()
        presenter.loadDeck(deckId)
    }

    override fun setupUI() {
        // Morphing the Step 4 UI into the Edit UI
        findViewById<TextView>(R.id.textviewHeaderTitle)?.text = "Review/Edit Mode"
        findViewById<TextView>(R.id.textviewStepNumber)?.visibility = View.GONE
        findViewById<TextView>(R.id.textviewFlashcardSetName)?.text = "Edit Deck"

        findViewById<LinearLayout>(R.id.linearlayoutMindMapVariant)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.linearlayoutFlashcardsVariant)?.visibility = View.VISIBLE

        findViewById<ImageView>(R.id.imageviewBack)?.setOnClickListener { finishActivity() }

        // Reusing the "Save" button at the top to just close the screen since DB saves instantly
        findViewById<TextView>(R.id.textviewSave)?.setOnClickListener {
            finishActivity()
        }
    }

    override fun showCards(cards: List<Flashcard>) {
        findViewById<TextView>(R.id.textviewCardCountBadge)?.text = "${cards.size} Cards"

        val container = findViewById<LinearLayout>(R.id.linearlayoutDynamicCards)
        container?.removeAllViews()

        val inflater = LayoutInflater.from(this)

        for (index in cards.indices) {
            val card = cards[index]
            val itemView = inflater.inflate(R.layout.item_flashcard_preview, container, false)

            itemView.findViewById<TextView>(R.id.textviewBadgeNumber)?.text = "${index + 1}"
            itemView.findViewById<TextView>(R.id.textviewCardTitle)?.text = "CARD ${index + 1}"
            itemView.findViewById<TextView>(R.id.textviewCardFront)?.text = card.frontText
            itemView.findViewById<TextView>(R.id.textviewCardBack)?.text = card.backText

            val contextText = card.contextText
            if (!contextText.isNullOrEmpty()) {
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.text = contextText
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.visibility = View.VISIBLE
                itemView.findViewById<LinearLayout>(R.id.linearlayoutContextPrompt)?.visibility = View.GONE
            } else {
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.visibility = View.GONE
                itemView.findViewById<LinearLayout>(R.id.linearlayoutContextPrompt)?.visibility = View.VISIBLE
            }

            // Bind click listeners to the Presenter
            itemView.findViewById<ImageView>(R.id.imageviewEditCard)?.setOnClickListener {
                presenter.onEditClicked(index)
            }

            itemView.findViewById<ImageView>(R.id.imageviewDeleteCard)?.setOnClickListener {
                presenter.onDeleteClicked(index)
            }

            itemView.findViewById<TextView>(R.id.textviewGenerateContext)?.setOnClickListener {
                presenter.onGenerateContextClicked(index)
            }

            itemView.findViewById<TextView>(R.id.textviewWriteManually)?.setOnClickListener {
                presenter.onManualContextClicked(index)
            }

            container?.addView(itemView)
        }
    }

    override fun showEditDialog(index: Int, card: Flashcard) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_edit_card, null)
        bottomSheetDialog.setContentView(view)

        val edittextEditFront = view.findViewById<EditText>(R.id.edittextEditFront)
        val edittextEditBack = view.findViewById<EditText>(R.id.edittextEditBack)
        val edittextEditContext = view.findViewById<EditText>(R.id.edittextEditContext)
        val textviewSaveCardChanges = view.findViewById<TextView>(R.id.textviewSaveCardChanges)
        val textviewCancel = view.findViewById<TextView>(R.id.textviewCancel)

        edittextEditFront.setText(card.frontText)
        edittextEditBack.setText(card.backText)
        edittextEditContext.setText(card.contextText ?: "")

        textviewCancel.setOnClickListener { bottomSheetDialog.dismiss() }

        textviewSaveCardChanges.setOnClickListener {
            val newFront = edittextEditFront.text.toString().trim()
            val newBack = edittextEditBack.text.toString().trim()
            val newContext = edittextEditContext.text.toString().trim().takeIf { it.isNotEmpty() }

            if (newFront.isEmpty() || newBack.isEmpty()) {
                showMessage("Front and Back text cannot be empty!")
                return@setOnClickListener
            }

            presenter.onSaveCardChanges(card.id, newFront, newBack, newContext)
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