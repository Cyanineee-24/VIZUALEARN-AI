package com.myApp.vizualearnfinal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mindmap_nodes")
data class MindMapNode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mindMapId: Int, // CHANGED: Now points to the MindMap, not StudySet
    val title: String,
    val description: String
)