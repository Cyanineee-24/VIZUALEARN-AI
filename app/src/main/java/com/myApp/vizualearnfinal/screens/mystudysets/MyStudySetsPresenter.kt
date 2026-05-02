package com.myApp.vizualearnfinal.screens.mystudysets

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.myApp.vizualearnfinal.data.model.StudySet

class MyStudySetsPresenter(
    private val view: MyStudySetsContract.View,
    private val model: MyStudySetsModel
) : MyStudySetsContract.Presenter {

    // Keep a master list in memory so we can filter instantly
    private var allSets: List<StudySet> = emptyList()
    private var currentFilter: String = "All"

    override fun loadSets() {
        CoroutineScope(Dispatchers.Main).launch {
            allSets = model.getAllSets()
            view.updateHeaderCount(allSets.size)

            // Re-apply whatever filter was currently selected
            filterSetsBySubject(currentFilter)
        }
    }

    override fun filterSetsBySubject(subject: String) {
        currentFilter = subject
        view.updateFilterUI(subject)

        val filteredList = if (subject == "All") {
            allSets
        } else {
            allSets.filter { it.subject.equals(subject, ignoreCase = true) }
        }

        if (filteredList.isEmpty()) {
            view.showEmptyState(true)
        } else {
            view.showEmptyState(false)
        }

        view.displayStudySets(filteredList)
    }
}