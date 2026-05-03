package com.myApp.vizualearnfinal.screens.step4

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode

class Step4Contract {
    interface View {
        fun showFlashcardsUI(setName: String, cards: List<Flashcard>)
        fun showMindMapUI(setName: String, nodes: List<MindMapNode>)
        fun showMessage(message: String)
        fun finishToDashboard()

        fun refreshFlashcardsList(cards: List<Flashcard>)
        fun showManualContextInput(index: Int)
        fun showEditCardDialog(index: Int, card: Flashcard?) // <--- Must be nullable (Flashcard?)
    }

    interface Presenter {
        fun initializeView(setId: Int, type: String, itemName: String, jsonResult: String)
        fun onSaveClicked()

        fun onGenerateContextClicked(index: Int)
        fun onManualContextClicked(index: Int)
        fun onManualContextSaved(index: Int, contextText: String)
        fun onEditCardClicked(index: Int)
        fun onDeleteCardClicked(index: Int)
        fun onEditCardSaved(index: Int, newFront: String, newBack: String, newContext: String?)

        // --- NEW FUNCTIONS ---
        fun onAddCardClicked()
        fun onAddCardSaved(front: String, back: String, contextText: String?)
        fun generateContextForText(front: String, back: String, callback: (String) -> Unit)
    }
}