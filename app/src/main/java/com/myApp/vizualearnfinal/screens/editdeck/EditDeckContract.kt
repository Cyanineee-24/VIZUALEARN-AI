package com.myApp.vizualearnfinal.screens.editdeck

import com.myApp.vizualearnfinal.data.model.Flashcard

interface EditDeckContract {
    interface View {
        fun setupUI()
        fun showCards(cards: List<Flashcard>)
        fun showEditDialog(index: Int, card: Flashcard)
        fun showMessage(message: String)
        fun finishActivity()
    }

    interface Presenter {
        fun loadDeck(deckId: Int)
        fun onEditClicked(index: Int)
        fun onDeleteClicked(index: Int)
        fun onSaveCardChanges(cardId: Int, newFront: String, newBack: String, newContext: String?)
        fun onGenerateContextClicked(index: Int)
        fun onManualContextClicked(index: Int)
    }
}