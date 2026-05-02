package com.myApp.vizualearnfinal.screens.dashboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val model: DashboardModel
) : DashboardContract.Presenter {

    override fun loadSets() {
        CoroutineScope(Dispatchers.Main).launch {
            val sets = model.getAllSets()
            if (sets.isEmpty()) {
                view.showEmptyState(true)
            } else {
                view.showEmptyState(false)
                view.displayStudySets(sets)
            }
        }
    }

    override fun loadUserData() {
        val name = model.getUserName()
        val streak = model.getUserStreak()
        view.displayUserData(name, streak)
    }

    override fun onCreateMindMapClicked() {
        view.navigateToSelectSet("mindmap")
    }

    override fun onCreateFlashCardsClicked() {
        view.navigateToSelectSet("flashcard")
    }

    override fun onAddSetClicked() {
        view.navigateToAddSet()
    }
}