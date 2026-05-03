package com.myApp.vizualearnfinal.screens.flashcardview

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class FlashCardViewModel(private val repository: StudySetRepository) {
    suspend fun getCardsForDeck(deckId: Int): List<Flashcard> {
        return repository.getFlashcardsForDeck(deckId)
    }

    suspend fun getParentStudySet(setId: Int): StudySet? {
        return repository.getSetById(setId)
    }
}