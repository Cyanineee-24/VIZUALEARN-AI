package com.myApp.vizualearnfinal.data.repository

import com.myApp.vizualearnfinal.data.database.StudySetDao
import com.myApp.vizualearnfinal.data.model.*

class StudySetRepository(private val dao: StudySetDao) {

    suspend fun insert(studySet: StudySet) = dao.insert(studySet)
    suspend fun getAllSets(): List<StudySet> = dao.getAllSets()
    suspend fun getSetById(setId: Int): StudySet? = dao.getSetById(setId)

    // --- FLASHCARDS ---
    suspend fun createDeckAndSaveCards(setId: Int, deckName: String, cards: List<Flashcard>) {
        // 1. Create the container deck
        val newDeck = FlashcardDeck(studySetId = setId, deckName = deckName)
        val newDeckId = dao.insertDeck(newDeck).toInt()

        // 2. Attach the new deck ID to all generated cards
        val cardsWithDeckId = cards.map { it.copy(deckId = newDeckId) }

        // 3. Save the cards and update the folder count
        dao.insertFlashcards(cardsWithDeckId)
        dao.updateFlashcardCount(setId, cards.size)
    }

    suspend fun getDecksForSet(setId: Int): List<FlashcardDeck> = dao.getDecksForSet(setId)
    suspend fun getFlashcardsForDeck(deckId: Int): List<Flashcard> = dao.getFlashcardsForDeck(deckId)

    // --- MIND MAPS ---
    suspend fun createMapAndSaveNodes(setId: Int, mapName: String, nodes: List<MindMapNode>) {
        val newMap = MindMap(studySetId = setId, mapName = mapName)
        val newMapId = dao.insertMindMap(newMap).toInt()

        val nodesWithMapId = nodes.map { it.copy(mindMapId = newMapId) }

        dao.insertMindMapNodes(nodesWithMapId)
        dao.updateMindMapCount(setId, 1)
    }

    suspend fun getMindMapsForSet(setId: Int): List<MindMap> = dao.getMindMapsForSet(setId)
    suspend fun getNodesForMap(mapId: Int): List<MindMapNode> = dao.getNodesForMap(mapId)

    suspend fun getFlashcardsForSet(setId: Int): List<Flashcard> {
        return dao.getFlashcardsForSet(setId)
    }

    suspend fun getMindMapNodesForSet(setId: Int): List<MindMapNode> {
        return dao.getMindMapNodesForSet(setId)
    }

    suspend fun updateFlashcard(card: Flashcard) {
        dao.updateFlashcard(card)
    }

    suspend fun deleteFlashcard(card: Flashcard) {
        dao.deleteFlashcard(card)
    }
}