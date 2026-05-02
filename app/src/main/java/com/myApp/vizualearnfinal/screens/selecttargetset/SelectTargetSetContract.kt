package com.myApp.vizualearnfinal.screens.selecttargetset

import com.myApp.vizualearnfinal.data.model.StudySet

class SelectTargetSetContract {
    interface View {
        fun displayStudySets(sets: List<StudySet>)
        fun showEmptyState(show: Boolean)
        fun navigateToStep1(setId: Int, type: String)
    }
    interface Presenter {
        fun loadSets()
        fun onSetSelected(setId: Int, type: String)
    }
}