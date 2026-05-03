package com.myApp.vizualearnfinal.screens.flashcardview

import android.content.Context
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.FlashcardDeck
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.DeckProgressManager

class FlashCardViewModel(
    private val context: Context,
    private val repository: StudySetRepository
) {
    suspend fun getCardsForDeck(deckId: Int): List<Flashcard> {
        return repository.getFlashcardsForDeck(deckId)
    }

    suspend fun getDeckById(deckId: Int): FlashcardDeck? {
        return repository.getDeckById(deckId)
    }

    suspend fun getParentStudySet(setId: Int): StudySet? {
        return repository.getSetById(setId)
    }

    suspend fun updateFlashcard(card: Flashcard) {
        repository.updateFlashcard(card)
    }

    // --- PROGRESS LOGIC ---
    fun getLearnedCardIds(deckId: Int): MutableSet<Int> {
        return DeckProgressManager.getLearnedIds(context, deckId)
    }

    fun saveLearnedCardIds(deckId: Int, ids: Set<Int>) {
        DeckProgressManager.saveLearnedIds(context, deckId, ids)
    }

    suspend fun saveProgressToDatabase(deckId: Int, totalCards: Int) {
        val progress = DeckProgressManager.getProgressPercent(context, deckId, totalCards)
        repository.updateDeckProgress(deckId, progress)
    }
}
