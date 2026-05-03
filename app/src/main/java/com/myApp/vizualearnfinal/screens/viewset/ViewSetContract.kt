package com.myApp.vizualearnfinal.screens.viewset

import com.myApp.vizualearnfinal.utils.DeckItem

interface ViewSetContract {
    interface View {
        fun displaySetHeader(setName: String, subtitle: String)
        fun displayDecks(flashcardDecks: List<DeckItem>, mindMapDecks: List<DeckItem>)
        fun getIconResourceId(iconName: String): Int // Let the View handle Android Resources
        fun navigateBack()
        fun navigateToStep1(setId: Int, type: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun loadSetData(setId: Int)
        fun onAddFlashcardClicked()
        fun onAddMindMapClicked()
    }
}