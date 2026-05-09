package com.myApp.vizualearnfinal.screens.editdeck

import android.content.Context
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.DeckProgressManager

class EditDeckModel(private val context: Context, private val repository: StudySetRepository) {

    suspend fun getCardsForDeck(deckId: Int): List<Flashcard> {
        return repository.getFlashcardsForDeck(deckId)
    }

    suspend fun updateFlashcard(card: Flashcard) {
        repository.updateFlashcard(card)
    }

    suspend fun deleteFlashcard(card: Flashcard) {
        repository.deleteFlashcard(card)
    }

    suspend fun insertFlashcard(card: Flashcard) {
        repository.insertFlashcard(card)
    }

    suspend fun removeCardFromLearned(deckId: Int, cardId: Int) {
        val learnedSet = DeckProgressManager.getLearnedIds(context, deckId)
        if (learnedSet.contains(cardId)) {
            learnedSet.remove(cardId)
            DeckProgressManager.saveLearnedIds(context, deckId, learnedSet)
        }
    }

    suspend fun syncDeckProgress(deckId: Int) {
        val totalCards = repository.getFlashcardsForDeck(deckId).size
        val progress = DeckProgressManager.getProgressPercent(context, deckId, totalCards)
        repository.updateDeckProgress(deckId, progress)
    }
}