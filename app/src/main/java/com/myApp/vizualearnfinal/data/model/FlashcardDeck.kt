package com.myApp.vizualearnfinal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_decks")
data class FlashcardDeck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studySetId: Int, // Points to the parent StudySet
    val deckName: String,
    val progress: Int = 0 // Default progress is 0%
)