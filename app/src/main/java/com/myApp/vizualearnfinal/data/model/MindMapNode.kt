package com.myApp.vizualearnfinal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mind_map_nodes")
data class MindMapNode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mindMapId: Int,
    val nodeId: String,    // NEW: Gemini's unique ID for the node
    val parentId: String,  // NEW: Defines the hierarchy (who it attaches to)
    val title: String,
    val description: String
)