package com.myApp.vizualearnfinal.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.model.StudySet

@Dao
interface StudySetDao {
    // --- STUDY SETS ---
    @Insert
    suspend fun insert(studySet: StudySet): Long

    @Query("SELECT * FROM study_sets ORDER BY timestamp DESC")
    suspend fun getAllSets(): List<StudySet>

    @Query("SELECT * FROM study_sets WHERE id = :setId LIMIT 1")
    suspend fun getSetById(setId: Int): StudySet?

    // Automatically updates the folder counts!
    @Query("UPDATE study_sets SET cardCount = cardCount + :count WHERE id = :setId")
    suspend fun updateFlashcardCount(setId: Int, count: Int)

    @Query("UPDATE study_sets SET mindMapCount = mindMapCount + :count WHERE id = :setId")
    suspend fun updateMindMapCount(setId: Int, count: Int)

    // --- FLASHCARDS ---
    @Insert
    suspend fun insertFlashcards(cards: List<Flashcard>)

    @Query("SELECT * FROM flashcards WHERE setId = :setId")
    suspend fun getFlashcardsForSet(setId: Int): List<Flashcard>

    // --- MIND MAP NODES ---
    @Insert
    suspend fun insertMindMapNodes(nodes: List<MindMapNode>)

    @Query("SELECT * FROM mindmap_nodes WHERE setId = :setId")
    suspend fun getMindMapNodesForSet(setId: Int): List<MindMapNode>
}