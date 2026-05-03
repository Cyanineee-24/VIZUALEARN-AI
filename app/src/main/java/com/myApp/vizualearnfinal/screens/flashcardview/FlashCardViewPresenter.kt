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

    private var studyQueue: ArrayDeque<Flashcard> = ArrayDeque()

    // FIX: This set is the source of truth for what's been learned this session
    private val learnedCardIds = mutableSetOf<Int>()

    private var peekIndex: Int = 0
    private var peekMode: Boolean = false
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
            originalCards = model.getCardsForDeck(deckId)

            if (originalCards.isEmpty()) {
                view.showMessage("No cards in this deck")
                view.finishActivity()
                return@launch
            }

            val deck = model.getDeckById(deckId)
            val deckTitle = deck?.deckName ?: "Deck View"

            val setTitle = if (deck != null) {
                model.getParentStudySet(deck.studySetId)?.setName ?: "Study Set"
            } else {
                "Study Set"
            }

            view.setHeaders(deckTitle, setTitle, originalCards.size)

            // FIX: Load previously saved learned IDs from SharedPreferences so
            // progress is restored if the user reopens the deck
            learnedCardIds.clear()
            learnedCardIds.addAll(model.getLearnedCardIds(deckId))

            resetStudySession()
        }
    }

    private fun resetStudySession() {
        // Only queue cards not yet learned
        val remainingCards = originalCards.filter { it.id !in learnedCardIds }
        studyQueue = ArrayDeque(remainingCards)
        peekIndex = 0
        peekMode = false
        isShowingFront = true

        view.updateProgressUI(learnedCardIds.size, originalCards.size)

        if (studyQueue.isEmpty() && learnedCardIds.isNotEmpty()) {
            view.showMessage("🎉 You've already learned all cards in this deck!")
        } else {
            showCurrentCard()
        }
    }

    private fun showCurrentCard() {
        if (studyQueue.isEmpty()) {
            view.showMessage("🎉 You've learned all cards!")
            return
        }
        val card = if (peekMode) studyQueue[peekIndex] else studyQueue.first()
        view.displayCard(card, isShowingFront, learnedCardIds.size, originalCards.size)
    }

    // FIX: Helper that persists progress to SharedPreferences immediately
    private fun persistProgress() {
        if (currentDeckId != -1) {
            model.saveLearnedCardIds(currentDeckId, learnedCardIds)
        }
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

        peekMode = false
        peekIndex = 0
        isShowingFront = true

        when (action) {
            "CORRECT" -> {
                val card = studyQueue.removeFirst()
                learnedCardIds.add(card.id)

                // FIX: Persist immediately so the progress is never lost
                persistProgress()

                view.updateProgressUI(learnedCardIds.size, originalCards.size)

                if (studyQueue.isEmpty()) {
                    view.showMessage("🎉 Deck complete! All cards learned!")
                    // Also update DB progress percentage
                    CoroutineScope(Dispatchers.Main).launch {
                        model.saveProgressToDatabase(currentDeckId, originalCards.size)
                    }
                } else {
                    showCurrentCard()
                }
            }
            "WRONG", "SKIP" -> {
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
        // FIX: Persist learned IDs and update DB progress before closing
        persistProgress()
        CoroutineScope(Dispatchers.Main).launch {
            model.saveProgressToDatabase(currentDeckId, originalCards.size)
            view.finishActivity()
        }
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
                val updated = mutableOriginal[idx].copy(
                    frontText = newFront,
                    backText = newBack,
                    contextText = newContext
                )
                model.updateFlashcard(updated)
                mutableOriginal[idx] = updated
                originalCards = mutableOriginal

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