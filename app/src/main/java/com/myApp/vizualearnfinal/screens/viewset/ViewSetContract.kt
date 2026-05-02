package com.myApp.vizualearnfinal.screens.viewset

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode

class ViewSetContract {
    interface View {
        fun displaySetHeader(setName: String, subject: String)
        fun displayFlashcards(cards: List<Flashcard>)
        fun displayMindMapNodes(nodes: List<MindMapNode>)
        fun navigateBack()
    }

    interface Presenter {
        fun loadSetData(setId: Int)
    }
}