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
    private val parsedFlashcards = mutableListOf<Flashcard>()
    private val parsedNodes = mutableListOf<MindMapNode>()

    override fun initializeView(setId: Int, type: String, jsonResult: String) {
        currentSetId = setId
        currentType = type

        CoroutineScope(Dispatchers.Main).launch {
            val studySet = model.getStudySet(setId)
            val setName = studySet?.setName ?: "Unknown Set"

            try {
                // Parse the JSON string from Gemini!
                val jsonArray = JSONArray(jsonResult)

                if (type == "mindmap") {
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        parsedNodes.add(
                            MindMapNode(
                                setId = setId,
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
                                setId = setId,
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
            if (currentType == "mindmap" && parsedNodes.isNotEmpty()) {
                model.saveMindMapNodes(currentSetId, parsedNodes)
            } else if (currentType == "flashcard" && parsedFlashcards.isNotEmpty()) {
                model.saveFlashcards(currentSetId, parsedFlashcards)
            }

            view.showMessage("Successfully saved to database!")
            view.finishToDashboard()
        }
    }
}