package com.myApp.vizualearnfinal.screens.editdeck

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class EditDeckModel(private val repository: StudySetRepository) {

    suspend fun getCardsForDeck(deckId: Int): List<Flashcard> {
        return repository.getFlashcardsForDeck(deckId)
    }

    suspend fun updateFlashcard(card: Flashcard) {
        repository.updateFlashcard(card)
    }

    suspend fun deleteFlashcard(card: Flashcard) {
        repository.deleteFlashcard(card)
    }

    // NEW: Needed so we can instantly save new cards to this existing deck!
    suspend fun insertFlashcard(card: Flashcard) {
        repository.insertFlashcard(card)
    }
}