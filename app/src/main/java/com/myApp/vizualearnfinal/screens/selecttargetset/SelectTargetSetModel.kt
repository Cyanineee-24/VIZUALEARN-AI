package com.myApp.vizualearnfinal.screens.selecttargetset

import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class SelectTargetSetModel(val repository: StudySetRepository) {
    suspend fun getAllSets(): List<StudySet> = repository.getAllSets()
}