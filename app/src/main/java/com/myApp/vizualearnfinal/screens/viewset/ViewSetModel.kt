package com.myApp.vizualearnfinal.screens.viewset

import android.content.Context
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.DeckProgressManager

class ViewSetModel(private val context: Context, private val repository: StudySetRepository) {
    suspend fun getStudySet(setId: Int): StudySet? = repository.getSetById(setId)

    // Flashcard DB Calls
    suspend fun getDecks(setId: Int) = repository.getDecksForSet(setId)
    suspend fun getDeckCardCount(deckId: Int) = repository.getFlashcardsForDeck(deckId).size
    fun getDeckProgress(deckId: Int, totalCards: Int): Int = DeckProgressManager.getProgressPercent(context, deckId, totalCards)
    suspend fun updateDeckProgress(deckId: Int, progress: Int) = repository.updateDeckProgress(deckId, progress)

    // Mind Map DB Calls
    suspend fun getMindMaps(setId: Int) = repository.getMindMapsForSet(setId)
    suspend fun getMapNodeCount(mapId: Int) = repository.getNodesForMap(mapId).size
}