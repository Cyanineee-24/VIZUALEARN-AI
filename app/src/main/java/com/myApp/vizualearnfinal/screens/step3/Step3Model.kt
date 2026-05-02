package com.myApp.vizualearnfinal.screens.step3


import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class Step3Model(val repository: StudySetRepository) {
    suspend fun getStudySet(setId: Int): StudySet? {
        return repository.getSetById(setId)
    }
}