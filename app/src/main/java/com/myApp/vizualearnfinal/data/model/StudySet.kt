package com.myApp.vizualearnfinal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName =  "study_sets")
data class StudySet (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val setName: String,
    val subject: String,
    val iconResName: String,
    val description: String,
    val cardCount: Int = 0,
    val mindMapCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)