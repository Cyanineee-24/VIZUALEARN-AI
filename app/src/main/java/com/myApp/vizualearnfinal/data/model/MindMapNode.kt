package com.myApp.vizualearnfinal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mind_map_nodes")
data class MindMapNode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mindMapId: Int,
    val nodeId: String,
    val parentId: String,
    val title: String,
    val description: String
)