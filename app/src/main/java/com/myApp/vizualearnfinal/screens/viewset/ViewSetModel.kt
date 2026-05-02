
package com.myApp.vizualearnfinal.screens.viewset

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class ViewSetModel(private val repository: StudySetRepository) {
    suspend fun getStudySet(setId: Int): StudySet? = repository.getSetById(setId)
    suspend fun getFlashcards(setId: Int): List<Flashcard> = repository.getFlashcardsForSet(setId)
    suspend fun getMindMapNodes(setId: Int): List<MindMapNode> = repository.getMindMapNodesForSet(setId)
}