package com.myApp.vizualearnfinal.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mindmap_nodes")
data class MindMapNode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val setId: Int, // The ID of the StudySet this node belongs to
    val title: String,
    val description: String
)
