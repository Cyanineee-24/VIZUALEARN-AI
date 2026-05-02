package com.myApp.vizualearnfinal.screens.addset

import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class AddSetModel(val repository: StudySetRepository) {
    suspend fun insertSet(studySet: StudySet) {
        repository.insert(studySet)
    }
}