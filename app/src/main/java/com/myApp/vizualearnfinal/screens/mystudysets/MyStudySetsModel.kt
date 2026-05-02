package com.myApp.vizualearnfinal.screens.mystudysets

import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class MyStudySetsModel(private val repository: StudySetRepository) {
    suspend fun getAllSets(): List<StudySet> {
        return repository.getAllSets()
    }
}