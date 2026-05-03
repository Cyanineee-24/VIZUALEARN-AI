package com.myApp.vizualearnfinal.screens.step4

import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class Step4Presenter(
    val view: Step4Contract.View,
    val model: Step4Model
) : Step4Contract.Presenter {

    private var currentSetId: Int = 0
    private var currentType: String = ""
    private var currentItemName: String = "" // Added to store the name

    private val parsedFlashcards = mutableListOf<Flashcard>()
    private val parsedNodes = mutableListOf<MindMapNode>()

    override fun initializeView(setId: Int, type: String, itemName: String, jsonResult: String) {
        currentSetId = setId
        currentType = type
        currentItemName = itemName // Store it for later

        CoroutineScope(Dispatchers.Main).launch {
            val studySet = model.getStudySet(setId)
            val setName = studySet?.setName ?: "Unknown Set"

            try {
                val jsonArray = JSONArray(jsonResult)

                if (type == "mindmap") {
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        parsedNodes.add(
                            MindMapNode(
                                mindMapId = 0, // Repository assigns the real ID later
                                title = obj.optString("title", "No Title"),
                                description = obj.optString("description", "No Description")
                            )
                        )
                    }
                    view.showMindMapUI(setName, parsedNodes)

                } else {
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        parsedFlashcards.add(
                            Flashcard(
                                deckId = 0, // Repository assigns the real ID later
                                frontText = obj.optString("frontText", "Error reading front"),
                                backText = obj.optString("backText", "Error reading back")
                            )
                        )
                    }
                    view.showFlashcardsUI(setName, parsedFlashcards)
                }
            } catch (e: Exception) {
                view.showMessage("Failed to parse Gemini data. Please try again.")
            }
        }
    }

    override fun onSaveClicked() {
        CoroutineScope(Dispatchers.Main).launch {
            // We now pass the currentItemName to the model to create the Folder/Deck!
            if (currentType == "mindmap" && parsedNodes.isNotEmpty()) {
                model.saveMindMap(currentSetId, currentItemName, parsedNodes)
            } else if (currentType == "flashcard" && parsedFlashcards.isNotEmpty()) {
                model.saveFlashcardDeck(currentSetId, currentItemName, parsedFlashcards)
            }
            view.showMessage("Successfully saved to database!")
            view.finishToDashboard()
        }
    }

    override fun onGenerateContextClicked(index: Int) {
        // Here you would eventually call the Gemini API again just like in Step 3!
        // For now, we will simulate a quick AI response.
        if (index in parsedFlashcards.indices) {
            val card = parsedFlashcards[index]
            view.showMessage("Gemini is analyzing context for Card ${index + 1}...")

            // Simulate AI updating the context
            val updatedCard = card.copy(contextText = "AI Generated Context: This is a detailed explanation about ${card.frontText}")
            parsedFlashcards[index] = updatedCard

            // Tell the UI to redraw with the new text
            view.refreshFlashcardsList(parsedFlashcards)
        }
    }

    override fun onManualContextClicked(index: Int) {
        view.showManualContextInput(index)
    }

    override fun onManualContextSaved(index: Int, contextText: String) {
        if (index in parsedFlashcards.indices && contextText.isNotBlank()) {
            val updatedCard = parsedFlashcards[index].copy(contextText = contextText)
            parsedFlashcards[index] = updatedCard
            view.refreshFlashcardsList(parsedFlashcards)
        }
    }

    override fun onEditCardClicked(index: Int) {
        // You would typically open a dialog here to edit the Front/Back text
        view.showMessage("Edit functionality coming soon for Card ${index + 1}")
    }

    override fun onDeleteCardClicked(index: Int) {
        if (index in parsedFlashcards.indices) {
            parsedFlashcards.removeAt(index)
            view.refreshFlashcardsList(parsedFlashcards)
            view.showMessage("Card deleted.")
        }
    }
}