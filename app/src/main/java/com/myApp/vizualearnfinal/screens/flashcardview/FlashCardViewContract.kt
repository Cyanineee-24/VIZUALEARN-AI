package com.myApp.vizualearnfinal.screens.flashcardview

import com.myApp.vizualearnfinal.data.model.Flashcard

interface FlashCardViewContract {
    interface View {
        fun setHeaders(deckTitle: String, parentSetTitle: String, totalCards: Int)
        fun displayCard(card: Flashcard, isShowingFront: Boolean, currentIndex: Int, totalCards: Int)
        fun updateProgressUI(learnedCount: Int, totalCards: Int)
        fun showMessage(message: String)
        fun finishActivity()

        // Navigation & UI
        fun navigateToEditDeck(deckId: Int)
        fun showEditCardUI(card: Flashcard) // RESTORED
    }

    interface Presenter {
        fun loadDeck(deckId: Int)
        fun onCardTapped()
        fun onNextClicked()
        fun onPrevClicked()
        fun onActionClicked(action: String)
        fun onModeSelected(mode: String)
        fun onDoneClicked()
        fun onReviewEditModeClicked()

        // Edit Actions RESTORED
        fun onEditCardClicked()
        fun saveEditedCard(cardId: Int, newFront: String, newBack: String, newContext: String?)
    }
}