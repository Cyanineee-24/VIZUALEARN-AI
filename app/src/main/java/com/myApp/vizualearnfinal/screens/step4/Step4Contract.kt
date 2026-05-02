package com.myApp.vizualearnfinal.screens.step4

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode

class Step4Contract {
    interface View {
        fun showFlashcardsUI(setName: String, cards: List<Flashcard>) // Updated
        fun showMindMapUI(setName: String, nodes: List<MindMapNode>) // Updated
        fun showMessage(message: String)
        fun finishToDashboard()
    }
    interface Presenter {
        // Now takes the JSON result!
        fun initializeView(setId: Int, type: String, jsonResult: String)
        fun onSaveClicked()
    }
}