package com.myApp.vizualearnfinal.screens.step4

import com.myApp.vizualearnfinal.data.model.*
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class Step4Model(val repository: StudySetRepository) {

    suspend fun getStudySet(setId: Int): StudySet? = repository.getSetById(setId)

    // NEW: We now save the entire Deck at once, not just raw cards
    suspend fun saveFlashcardDeck(setId: Int, deckName: String, cards: List<Flashcard>) {
        repository.createDeckAndSaveCards(setId, deckName, cards)
    }

    // NEW: Same for Mind Maps
    suspend fun saveMindMap(setId: Int, mapName: String, nodes: List<MindMapNode>) {
        repository.createMapAndSaveNodes(setId, mapName, nodes)
    }
}