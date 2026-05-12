package com.myApp.vizualearnfinal.screens.viewset

import com.myApp.vizualearnfinal.utils.ContainerType
import com.myApp.vizualearnfinal.utils.DeckItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewSetPresenter(
    private val view: ViewSetContract.View,
    private val model: ViewSetModel
) : ViewSetContract.Presenter {

    private var currentSetId: Int = -1

    override fun loadSetData(setId: Int) {
        this.currentSetId = setId

        CoroutineScope(Dispatchers.Main).launch {
            val studySet = model.getStudySet(setId)
            if (studySet == null) {
                view.showError("Error loading set details.")
                return@launch
            }

            view.displaySetHeader(studySet.setName, "Items inside this set")

            // Ask the View to turn the string name into an Android Icon ID
            val iconName = studySet.iconResName ?: "ic_book"
            val dynamicIconId = view.getIconResourceId(iconName)

            // Build the Flashcard UI Items
            val dbDecks = model.getDecks(setId)
            val flashcardItems = dbDecks.map { deck ->
                val cardCount = model.getDeckCardCount(deck.id)
                val progress = model.getDeckProgress(deck.id, cardCount)
                model.updateDeckProgress(deck.id, progress) // Keep DB synced

                DeckItem(
                    id = deck.id,
                    title = deck.deckName,
                    subtitle = "$cardCount Cards",
                    progress = progress,
                    type = ContainerType.FLASHCARD,
                    iconResId = dynamicIconId
                )
            }

            // Build the Mind Map UI Items
            val dbMaps = model.getMindMaps(setId)
            val mindMapItems = dbMaps.map { map ->
                val nodeCount = model.getMapNodeCount(map.id)
                DeckItem(
                    id = map.id,
                    title = map.mapName,
                    subtitle = "$nodeCount nodes in total",
                    progress = 0,
                    type = ContainerType.MIND_MAP,
                    iconResId = dynamicIconId
                )
            }

            // Send to View
            view.displayDecks(flashcardItems, mindMapItems)
        }
    }

    override fun onAddFlashcardClicked() {
        if (currentSetId != -1) {
            view.navigateToStep1(currentSetId, "flashcard")
        } else {
            view.showError("Error: Set ID not found.")
        }
    }

    override fun onAddMindMapClicked() {
        if (currentSetId != -1) {
            view.navigateToStep1(currentSetId, "mindmap")
        } else {
            view.showError("Error: Set ID not found.")
        }
    }
}