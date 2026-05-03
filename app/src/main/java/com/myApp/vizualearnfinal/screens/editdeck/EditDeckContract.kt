package com.myApp.vizualearnfinal.screens.editdeck

import com.myApp.vizualearnfinal.data.model.Flashcard

interface EditDeckContract {
    interface View {
        fun setupUI()
        fun showCards(cards: List<Flashcard>)
        fun showMessage(message: String)
        fun finishActivity()
        fun showEditCardDialog(index: Int, card: Flashcard?)
    }

    interface Presenter {
        fun loadDeck(deckId: Int)
        fun onEditClicked(index: Int)
        fun onDeleteClicked(index: Int)
        fun onEditCardSaved(index: Int, newFront: String, newBack: String, newContext: String?)
        fun onGenerateContextClicked(index: Int)
        fun onManualContextClicked(index: Int)
        fun onAddCardClicked()
        fun onAddCardSaved(front: String, back: String, contextText: String?)
        fun generateContextForText(front: String, back: String, callback: (String) -> Unit)
    }
}