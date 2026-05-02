package com.myApp.vizualearnfinal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val setId: Int, // The ID of the StudySet this card belongs to
    val frontText: String,
    val backText: String
)