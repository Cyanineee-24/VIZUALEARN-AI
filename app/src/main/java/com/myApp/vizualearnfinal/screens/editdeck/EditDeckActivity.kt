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
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.toast

class EditDeckActivity : AppCompatActivity(), EditDeckContract.View {

    private lateinit var presenter: EditDeckContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step4_result)

        val deckId = intent.getIntExtra("EXTRA_DECK_ID", -1)

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)

        presenter = EditDeckPresenter(this, EditDeckModel(this, repository))

        setupUI()
        presenter.loadDeck(deckId)

        getTextView(R.id.textviewAddCard)?.setOnClickListener {
            presenter.onAddCardClicked()
        }
    }

    override fun setupUI() {
        findViewById<TextView>(R.id.textviewHeaderTitle)?.text = "Review/Edit Mode"
        findViewById<TextView>(R.id.textviewStepNumber)?.visibility = View.GONE
        findViewById<TextView>(R.id.textviewFlashcardSetName)?.text = "Edit Deck"

        findViewById<LinearLayout>(R.id.linearlayoutMindMapVariant)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.linearlayoutFlashcardsVariant)?.visibility = View.VISIBLE

        findViewById<ImageView>(R.id.imageviewBack)?.setOnClickListener { finishActivity() }

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

    override fun showEditCardDialog(index: Int, card: Flashcard?) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_edit_card, null)
        bottomSheetDialog.setContentView(view)

        val edittextEditFront = view.findViewById<EditText>(R.id.edittextEditFront)
        val edittextEditBack = view.findViewById<EditText>(R.id.edittextEditBack)
        val edittextEditContext = view.findViewById<EditText>(R.id.edittextEditContext)
        val textviewSaveCardChanges = view.findViewById<TextView>(R.id.textviewSaveCardChanges)
        val textviewCancel = view.findViewById<TextView>(R.id.textviewCancel)
        val btnGenerateAi = view.findViewById<LinearLayout>(R.id.btnGenerateAiBottomSheet)

        if (card == null) {
            view.findViewById<TextView>(R.id.textviewEditTitle).text = "Add New Card"
        } else {
            edittextEditFront.setText(card.frontText)
            edittextEditBack.setText(card.backText)
            edittextEditContext.setText(card.contextText ?: "")
        }

        btnGenerateAi.setOnClickListener {
            val front = edittextEditFront.text.toString().trim()
            val back = edittextEditBack.text.toString().trim()
            if (front.isEmpty() || back.isEmpty()) {
                toast("Please fill out the Front and Back first!")
                return@setOnClickListener
            }
            toast("Gemini is writing context...")
            presenter.generateContextForText(front, back) { generatedText ->
                edittextEditContext.setText(generatedText)
            }
        }

        textviewCancel.setOnClickListener { bottomSheetDialog.dismiss() }

        textviewSaveCardChanges.setOnClickListener {
            val newFront = edittextEditFront.text.toString().trim()
            val newBack = edittextEditBack.text.toString().trim()
            val newContext = edittextEditContext.text.toString().trim().takeIf { it.isNotEmpty() }

            if (newFront.isEmpty() || newBack.isEmpty()) {
                toast("Front and Back cannot be empty!")
                return@setOnClickListener
            }

            if (card == null) {
                presenter.onAddCardSaved(newFront, newBack, newContext)
            } else {
                presenter.onEditCardSaved(index, newFront, newBack, newContext)
            }
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