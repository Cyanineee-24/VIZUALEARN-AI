package com.myApp.vizualearnfinal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.FlashcardDeck
import com.myApp.vizualearnfinal.data.model.MindMap
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.model.StudySet

// 1. Added Flashcard and MindMapNode to entities, bumped version to 3
@Database(
    entities = [StudySet::class, FlashcardDeck::class, Flashcard::class, MindMap::class, MindMapNode::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studySetDao(): StudySetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vizualearn_database"
                )
                    .fallbackToDestructiveMigration() // 2. Prevents crashes when schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}