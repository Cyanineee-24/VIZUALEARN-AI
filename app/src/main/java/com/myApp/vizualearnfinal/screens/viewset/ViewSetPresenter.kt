package com.myApp.vizualearnfinal.screens.viewset

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewSetPresenter(
    private val view: ViewSetContract.View,
    private val model: ViewSetModel
) : ViewSetContract.Presenter {

    override fun loadSetData(setId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            // Fetch everything related to this specific Set ID
            val studySet = model.getStudySet(setId)
            val flashcards = model.getFlashcards(setId)
            val mindMapNodes = model.getMindMapNodes(setId)

            if (studySet != null) {
                view.displaySetHeader(studySet.setName, "${studySet.cardCount} Sets Created")
            }

            view.displayFlashcards(flashcards)
            view.displayMindMapNodes(mindMapNodes)
        }
    }
}