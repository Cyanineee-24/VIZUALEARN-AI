package com.myApp.vizualearnfinal.screens.mystudysets

import com.myApp.vizualearnfinal.data.model.StudySet

interface MyStudySetsContract {
    interface View {
        fun displayStudySets(sets: List<StudySet>)
        fun showEmptyState(show: Boolean)
        fun updateFilterUI(selectedSubject: String)
        fun updateHeaderCount(count: Int)
    }

    interface Presenter {
        fun loadSets()
        fun filterSetsBySubject(subject: String)
    }
}