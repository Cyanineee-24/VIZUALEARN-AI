package com.myApp.vizualearnfinal.screens.flashcardview

import com.myApp.vizualearnfinal.data.model.Flashcard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
 import kotlinx.coroutines.withContext

class FlashCardViewPresenter(
    private val view: FlashCardViewContract.View,
    private val model: FlashCardViewModel
) : FlashCardViewContract.Presenter {

    private var currentMode: String = "Study Mode"
    private var originalCards: List<Flashcard> = emptyList()

    // The active queue (cards still to be learned)
    private var studyQueue: ArrayDeque<Flashcard> = ArrayDeque()

    // Cards that have been "Got It!"
    private val learnedCardIds = mutableSetOf<Int>()

    // For peeking (prev/next) without affecting queue
    private var peekIndex: Int = 0
    private var peekMode: Boolean = false  // true when user is browsing with arrows
    private var isShowingFront: Boolean = true

    private var currentDeckId: Int = -1

    override fun loadDeck(deckId: Int) {
        currentDeckId = deckId
        if (deckId == -1) { 
            view.showMessage("Error loading deck")
            view.finishActivity()
            return 
        }

        CoroutineScope(Dispatchers.Main).launch {
            // 1. Load cards
            originalCards = model.getCardsForDeck(deckId)
            
            if (originalCards.isEmpty()) { 
                view.showMessage("No cards in this deck")
                view.finishActivity()
                return@launch 
            }

            // 2. Load Deck and Study Set details (Suspended calls moved inside coroutine)
            val deck = model.getDeckById(deckId)
            val deckTitle = deck?.deckName ?: "Deck View"
            
            val setTitle = if (deck != null) {
                model.getParentStudySet(deck.studySetId)?.setName ?: "Study Set"
            } else {
                "Study Set"
            }

            // 3. Update UI
            view.setHeaders(deckTitle, setTitle, originalCards.size)
            resetStudySession()
        }
    }

    private fun resetStudySession() {
        studyQueue = ArrayDeque(originalCards)
        peekIndex = 0
        peekMode = false
        isShowingFront = true
        learnedCardIds.clear()

        view.updateProgressUI(0, originalCards.size)
        showCurrentCard()
    }

    private fun showCurrentCard() {
        if (studyQueue.isEmpty()) {
            view.showMessage("🎉 You've learned all cards!")
            return
        }
        val card = if (peekMode) studyQueue[peekIndex] else studyQueue.first()
        view.displayCard(card, isShowingFront,
            learnedCardIds.size, originalCards.size)
    }

    override fun onCardTapped() {
        isShowingFront = !isShowingFront
        showCurrentCard()
    }

    override fun onNextClicked() {
        if (studyQueue.isEmpty()) return
        peekMode = true
        if (peekIndex < studyQueue.size - 1) peekIndex++
        isShowingFront = true
        showCurrentCard()
    }

    override fun onPrevClicked() {
        if (studyQueue.isEmpty()) return
        peekMode = true
        if (peekIndex > 0) peekIndex--
        isShowingFront = true
        showCurrentCard()
    }

    override fun onActionClicked(action: String) {
        if (studyQueue.isEmpty()) return

        // If peeking, bring focus back to front of queue first
        peekMode = false
        peekIndex = 0
        isShowingFront = true

        when (action) {
            "CORRECT" -> {
                val card = studyQueue.removeFirst()
                learnedCardIds.add(card.id)
                view.updateProgressUI(learnedCardIds.size, originalCards.size)

                if (studyQueue.isEmpty() && learnedCardIds.size == originalCards.size) {
                    view.showMessage("🎉 Deck complete! All cards learned!")
                } else {
                    showCurrentCard()
                }
            }
            "WRONG", "SKIP" -> {
                // Move current card to end of queue
                val card = studyQueue.removeFirst()
                studyQueue.addLast(card)
                showCurrentCard()
            }
        }
    }

    override fun onModeSelected(mode: String) {
        if (currentMode == mode) { 
            view.showMessage("Already on $mode")
            return 
        }
        currentMode = mode

        val remainingCards = originalCards.filter { it.id !in learnedCardIds }
        studyQueue = ArrayDeque(if (mode == "Quiz Mode++") remainingCards.shuffled() else remainingCards)
        peekIndex = 0
        peekMode = false
        isShowingFront = true

        view.updateProgressUI(learnedCardIds.size, originalCards.size)
        showCurrentCard()
        view.showMessage("Switched to $mode")
    }

    override fun onDoneClicked() {
        // The ArrayDeque and DeckProgressManager have already been tracking and saving
        // the learnedCardIds seamlessly behind the scenes. We just close the view!
        view.finishActivity()
    }

    override fun onReviewEditModeClicked() {
        val deckId = originalCards.firstOrNull()?.deckId ?: -1
        if (deckId != -1) view.navigateToEditDeck(deckId)
        else view.showMessage("Error: Cannot find Deck ID.")
    }

    override fun onEditCardClicked() {
        if (studyQueue.isNotEmpty()) {
            val card = if (peekMode) studyQueue[peekIndex] else studyQueue.first()
            view.showEditCardUI(card)
        }
    }

    override fun saveEditedCard(cardId: Int, newFront: String, newBack: String, newContext: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            val mutableOriginal = originalCards.toMutableList()
            val idx = mutableOriginal.indexOfFirst { it.id == cardId }
            if (idx != -1) {
                val updated = mutableOriginal[idx].copy(frontText = newFront, backText = newBack, contextText = newContext)
                model.updateFlashcard(updated)
                mutableOriginal[idx] = updated
                originalCards = mutableOriginal

                // Also update in queue
                val queueIdx = studyQueue.indexOfFirst { it.id == cardId }
                if (queueIdx != -1) {
                    studyQueue[queueIdx] = updated
                }

                view.showMessage("Card updated!")
                showCurrentCard()
            }
        }
    }
}
