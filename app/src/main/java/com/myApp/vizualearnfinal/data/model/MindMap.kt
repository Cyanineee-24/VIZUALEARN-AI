package com.myApp.vizualearnfinal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mind_maps")
data class MindMap(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studySetId: Int, // Points to the parent StudySet
    val mapName: String
)