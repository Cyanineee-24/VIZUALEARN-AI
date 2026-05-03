package com.myApp.vizualearnfinal.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.myApp.vizualearnfinal.data.model.*

@Dao
interface StudySetDao {

    // --- STUDY SETS ---
    @Insert
    suspend fun insert(studySet: StudySet): Long

    @Query("SELECT * FROM study_sets ORDER BY timestamp DESC")
    suspend fun getAllSets(): List<StudySet>

    @Query("SELECT * FROM study_sets WHERE id = :setId LIMIT 1")
    suspend fun getSetById(setId: Int): StudySet?

    @Query("UPDATE study_sets SET cardCount = cardCount + :count WHERE id = :setId")
    suspend fun updateFlashcardCount(setId: Int, count: Int)

    @Query("UPDATE study_sets SET mindMapCount = mindMapCount + :count WHERE id = :setId")
    suspend fun updateMindMapCount(setId: Int, count: Int)

    // --- DECKS & FLASHCARDS ---
    @Insert
    suspend fun insertDeck(deck: FlashcardDeck): Long // Returns the new Deck ID

    @Query("SELECT * FROM flashcard_decks WHERE studySetId = :setId")
    suspend fun getDecksForSet(setId: Int): List<FlashcardDeck>

    @Insert
    suspend fun insertFlashcards(cards: List<Flashcard>)

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    suspend fun getFlashcardsForDeck(deckId: Int): List<Flashcard>

    // --- MAPS & NODES ---
    @Insert
    suspend fun insertMindMap(mindMap: MindMap): Long // Returns the new Map ID

    @Query("SELECT * FROM mind_maps WHERE studySetId = :setId")
    suspend fun getMindMapsForSet(setId: Int): List<MindMap>

    @Insert
    suspend fun insertMindMapNodes(nodes: List<MindMapNode>)

    @Query("SELECT * FROM mind_map_nodes WHERE mindMapId = :mapId")
    suspend fun getNodesForMap(mapId: Int): List<MindMapNode>

    // Gets all flashcards that belong to any deck inside a specific Study Set
    @Query("""
        SELECT flashcards.* FROM flashcards 
        INNER JOIN flashcard_decks ON flashcards.deckId = flashcard_decks.id 
        WHERE flashcard_decks.studySetId = :setId
    """)
    suspend fun getFlashcardsForSet(setId: Int): List<Flashcard>

    // Gets all nodes that belong to any mind map inside a specific Study Set
    @Query("""
        SELECT mind_map_nodes.* FROM mind_map_nodes 
        INNER JOIN mind_maps ON mind_map_nodes.mindMapId = mind_maps.id
        WHERE mind_maps.studySetId = :setId
    """)
    suspend fun getMindMapNodesForSet(setId: Int): List<MindMapNode>

    @Update
    suspend fun updateFlashcard(card: Flashcard)

    @Delete
    suspend fun deleteFlashcard(card: Flashcard)

    @Query("SELECT * FROM flashcard_decks WHERE id = :deckId LIMIT 1")
    suspend fun getDeckById(deckId: Int): FlashcardDeck?

    @Query("UPDATE flashcard_decks SET progress = :progress WHERE id = :deckId")
    suspend fun updateDeckProgress(deckId: Int, progress: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard): Long
}