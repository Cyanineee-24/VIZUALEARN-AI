package com.myApp.vizualearnfinal.screens.dashboard

import com.myApp.vizualearnfinal.data.model.StudySet

class DashboardContract {
    interface View {
        fun displayStudySets(sets: List<StudySet>)
        fun showEmptyState(show: Boolean)
        fun displayUserData(userName: String, streakDays: Int)
        fun navigateToSelectSet(creationType: String)
        fun navigateToAddSet()
    }

    interface Presenter {
        fun loadSets()
        fun loadUserData()
        fun onCreateMindMapClicked()
        fun onCreateFlashCardsClicked()
        fun onAddSetClicked()
    }
}