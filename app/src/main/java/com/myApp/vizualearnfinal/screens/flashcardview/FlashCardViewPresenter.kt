package com.myApp.vizualearnfinal.screens.flashcardview

import com.myApp.vizualearnfinal.data.model.Flashcard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FlashCardViewPresenter(
    private val view: FlashCardViewContract.View,
    private val model: FlashCardViewModel
) : FlashCardViewContract.Presenter {

    private var currentMode: String = "Study Mode"
    private var cards: List<Flashcard> = emptyList()
    private var currentIndex: Int = 0
    private var isShowingFront: Boolean = true
    private var learnedCount: Int = 0

    override fun loadDeck(deckId: Int) {
        if (deckId == -1) {
            view.showMessage("Error loading deck")
            view.finishActivity()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            cards = model.getCardsForDeck(deckId)

            if (cards.isEmpty()) {
                view.showMessage("No cards in this deck")
                view.finishActivity()
                return@launch
            }

            view.setHeaders("Deck View", "Study Set", cards.size)
            updateCardDisplay()
            view.updateProgressUI(learnedCount, cards.size)
        }
    }

    override fun onCardTapped() {
        isShowingFront = !isShowingFront
        updateCardDisplay()
    }

    override fun onNextClicked() {
        if (currentIndex < cards.size - 1) {
            currentIndex++
            isShowingFront = true
            updateCardDisplay()
        } else {
            view.showMessage("You've reached the end of the deck!")
        }
    }

    override fun onPrevClicked() {
        if (currentIndex > 0) {
            currentIndex--
            isShowingFront = true
            updateCardDisplay()
        }
    }

    override fun onActionClicked(action: String) {
        when (action) {
            "CORRECT" -> {
                learnedCount++
                view.updateProgressUI(learnedCount, cards.size)
                onNextClicked()
            }
            "WRONG", "SKIP" -> {
                onNextClicked()
            }
        }
    }

    override fun onModeSelected(mode: String) {
        if (currentMode == mode) {
            view.showMessage("You are already on $mode")
        } else {
            currentMode = mode
            view.showMessage("Switched to $mode")
        }
    }

    override fun onEditCardClicked() {
        if (cards.isNotEmpty()) {
            val currentCard = cards[currentIndex]
            view.showEditCardUI(currentCard)
        }
    }

    override fun saveEditedCard(cardId: Int, newFront: String, newBack: String, newContext: String?) {
        val cardIndex = cards.indexOfFirst { it.id == cardId }

        if (cardIndex != -1) {
            val updatedCard = cards[cardIndex].copy(
                frontText = newFront,
                backText = newBack,
                contextText = newContext
            )

            val mutableCards = cards.toMutableList()
            mutableCards[cardIndex] = updatedCard
            cards = mutableCards.toList()

            view.showMessage("Card updated successfully!")
            updateCardDisplay()
        }
    }

    private fun updateCardDisplay() {
        if (cards.isNotEmpty()) {
            view.displayCard(cards[currentIndex], isShowingFront, currentIndex, cards.size)
        }
    }
}