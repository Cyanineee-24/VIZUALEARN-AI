package com.myApp.vizualearnfinal.screens.step1



import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class Step1Model(val repository: StudySetRepository) {
    suspend fun getStudySet(setId: Int): StudySet? {
        return repository.getSetById(setId)
    }
}