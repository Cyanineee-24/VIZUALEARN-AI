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
    private var originalCards: List<Flashcard> = emptyList()
    private var displayedCards: List<Flashcard> = emptyList()
    private var currentIndex: Int = 0
    private var isShowingFront: Boolean = true
    private val learnedCards = mutableSetOf<Int>()

    override fun loadDeck(deckId: Int) {
        if (deckId == -1) {
            view.showMessage("Error loading deck")
            view.finishActivity()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            originalCards = model.getCardsForDeck(deckId)

            if (originalCards.isEmpty()) {
                view.showMessage("No cards in this deck")
                view.finishActivity()
                return@launch
            }

            displayedCards = originalCards.toList()
            view.setHeaders("Deck View", "Study Set", originalCards.size)
            updateCardDisplay()
            view.updateProgressUI(learnedCards.size, displayedCards.size)
        }
    }

    override fun onCardTapped() {
        isShowingFront = !isShowingFront
        updateCardDisplay()
    }

    override fun onNextClicked() {
        if (currentIndex < displayedCards.size - 1) {
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
                learnedCards.add(displayedCards[currentIndex].id)
                view.updateProgressUI(learnedCards.size, displayedCards.size)
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
            return
        }

        currentMode = mode
        currentIndex = 0
        isShowingFront = true
        learnedCards.clear()

        displayedCards = if (mode == "Quiz Mode++") {
            originalCards.shuffled()
        } else {
            originalCards.toList()
        }

        view.updateProgressUI(0, displayedCards.size)
        updateCardDisplay()
        view.showMessage("Switched to $mode")
    }

    override fun onReviewEditModeClicked() {
        val currentDeckId = originalCards.firstOrNull()?.deckId ?: -1
        if (currentDeckId != -1) {
            view.navigateToEditDeck(currentDeckId)
        } else {
            view.showMessage("Error: Cannot find Deck ID.")
        }
    }

    // --- RESTORED SINGLE CARD EDIT LOGIC ---

    override fun onEditCardClicked() {
        if (displayedCards.isNotEmpty()) {
            val currentCard = displayedCards[currentIndex]
            view.showEditCardUI(currentCard)
        }
    }

    override fun saveEditedCard(cardId: Int, newFront: String, newBack: String, newContext: String?) {
        val cardIndex = originalCards.indexOfFirst { it.id == cardId }

        if (cardIndex != -1) {
            val updatedCard = originalCards[cardIndex].copy(
                frontText = newFront,
                backText = newBack,
                contextText = newContext
            )

            // Save to DB in background, then update UI
            CoroutineScope(Dispatchers.Main).launch {
                model.updateFlashcard(updatedCard) // Update Database

                // Update Local State
                val mutableCards = originalCards.toMutableList()
                mutableCards[cardIndex] = updatedCard
                originalCards = mutableCards.toList()

                // Re-sync displayed cards
                displayedCards = if (currentMode == "Quiz Mode++") {
                    displayedCards.map { if (it.id == cardId) updatedCard else it }
                } else {
                    originalCards.toList()
                }

                view.showMessage("Card updated successfully!")
                updateCardDisplay()
            }
        }
    }

    private fun updateCardDisplay() {
        if (displayedCards.isNotEmpty()) {
            view.displayCard(displayedCards[currentIndex], isShowingFront, currentIndex, displayedCards.size)
        }
    }
}