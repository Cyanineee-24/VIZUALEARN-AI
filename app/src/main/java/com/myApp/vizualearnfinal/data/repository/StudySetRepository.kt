package com.myApp.vizualearnfinal.data.repository

import com.myApp.vizualearnfinal.data.database.StudySetDao
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.model.StudySet

class StudySetRepository(private val dao: StudySetDao) {
    suspend fun insert(studySet: StudySet) = dao.insert(studySet)
    suspend fun getAllSets(): List<StudySet> = dao.getAllSets()
    suspend fun getSetById(setId: Int): StudySet? = dao.getSetById(setId)

    // --- FLASHCARDS ---
    suspend fun insertFlashcards(setId: Int, cards: List<Flashcard>) {
        dao.insertFlashcards(cards)
        dao.updateFlashcardCount(setId, cards.size) // Automatically updates the folder's UI!
    }
    suspend fun getFlashcardsForSet(setId: Int): List<Flashcard> = dao.getFlashcardsForSet(setId)

    // --- MIND MAPS ---
    suspend fun insertMindMapNodes(setId: Int, nodes: List<MindMapNode>) {
        dao.insertMindMapNodes(nodes)
        dao.updateMindMapCount(setId, 1) // Increments the mind map count by 1
    }
    suspend fun getMindMapNodesForSet(setId: Int): List<MindMapNode> = dao.getMindMapNodesForSet(setId)
}