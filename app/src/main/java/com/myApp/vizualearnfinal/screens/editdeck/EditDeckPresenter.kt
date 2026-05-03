package com.myApp.vizualearnfinal.screens.editdeck

import com.myApp.vizualearnfinal.data.model.Flashcard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditDeckPresenter(
    private val view: EditDeckContract.View,
    private val model: EditDeckModel
) : EditDeckContract.Presenter {

    private var currentCards = mutableListOf<Flashcard>()
    private var currentDeckId: Int = -1

    override fun loadDeck(deckId: Int) {
        currentDeckId = deckId
        CoroutineScope(Dispatchers.Main).launch {
            currentCards.clear()
            currentCards.addAll(model.getCardsForDeck(deckId))
            view.showCards(currentCards)
        }
    }

    override fun onEditClicked(index: Int) {
        if (index in currentCards.indices) {
            view.showEditDialog(index, currentCards[index])
        }
    }

    override fun onDeleteClicked(index: Int) {
        if (index in currentCards.indices) {
            val cardToDelete = currentCards[index]
            CoroutineScope(Dispatchers.Main).launch {
                model.deleteFlashcard(cardToDelete)
                currentCards.removeAt(index)
                view.showCards(currentCards)
                view.showMessage("Card deleted.")
            }
        }
    }

    override fun onSaveCardChanges(cardId: Int, newFront: String, newBack: String, newContext: String?) {
        val index = currentCards.indexOfFirst { it.id == cardId }
        if (index != -1) {
            val updatedCard = currentCards[index].copy(
                frontText = newFront,
                backText = newBack,
                contextText = newContext
            )
            CoroutineScope(Dispatchers.Main).launch {
                model.updateFlashcard(updatedCard)
                currentCards[index] = updatedCard
                view.showCards(currentCards)
                view.showMessage("Card updated successfully!")
            }
        }
    }

    override fun onGenerateContextClicked(index: Int) {
        // You can wire up the GenerativeModel here just like Step4Presenter later
        view.showMessage("AI Context Generation coming soon!")
    }

    override fun onManualContextClicked(index: Int) {
        if (index in currentCards.indices) {
            // For now, we just open the bottom sheet so they can type it in the Context box
            view.showEditDialog(index, currentCards[index])
        }
    }
}