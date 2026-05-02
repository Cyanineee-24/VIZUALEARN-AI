package com.myApp.vizualearnfinal.screens.step4

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class Step4Model(val repository: StudySetRepository) {
    suspend fun getStudySet(setId: Int): StudySet? = repository.getSetById(setId)

    // Add our new insert functions from Phase 1!
    suspend fun saveFlashcards(setId: Int, cards: List<Flashcard>) {
        repository.insertFlashcards(setId, cards)
    }

    suspend fun saveMindMapNodes(setId: Int, nodes: List<MindMapNode>) {
        repository.insertMindMapNodes(setId, nodes)
    }
}