package com.myApp.vizualearnfinal.screens.selecttargetset


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectTargetSetPresenter(
    val view: SelectTargetSetContract.View,
    val model: SelectTargetSetModel
) : SelectTargetSetContract.Presenter {
    override fun loadSets() {
        CoroutineScope(Dispatchers.Main).launch {
            val sets = model.getAllSets()
            view.showEmptyState(sets.isEmpty())
            if (sets.isNotEmpty()) view.displayStudySets(sets)
        }
    }
    override fun onSetSelected(setId: Int, type: String) {
        view.navigateToStep1(setId, type)
    }
}