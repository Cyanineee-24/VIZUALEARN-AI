package com.myApp.vizualearnfinal.screens.mindmapview

import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class MindMapViewModel(private val repository: StudySetRepository) {
    suspend fun getNodes(mapId: Int): List<MindMapNode> {
        return repository.getNodesForMap(mapId)
    }
}