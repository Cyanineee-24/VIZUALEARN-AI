package com.myApp.vizualearnfinal.screens.step2


import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository

class Step2Model(val repository: StudySetRepository) {
    suspend fun getStudySet(setId: Int): StudySet? {
        return repository.getSetById(setId)
    }
}