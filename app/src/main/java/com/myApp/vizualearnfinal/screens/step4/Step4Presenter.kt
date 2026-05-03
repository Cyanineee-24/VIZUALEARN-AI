package com.myApp.vizualearnfinal.screens.step4

import com.google.ai.client.generativeai.GenerativeModel
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = Constants.GEMINI_API_KEY
    )

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
                                mindMapId = 0,
                                nodeId = obj.optString("id", "node_$i"),      // <--- CATCH ID
                                parentId = obj.optString("parentId", "root"), // <--- CATCH PARENT
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
        if (index !in parsedFlashcards.indices) return
        val card = parsedFlashcards[index]
        view.showMessage("Gemini is analyzing Card ${index + 1}...")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prompt = """
                You are an expert educator helping a student deeply understand a concept.
                
                The student has this flashcard:
                QUESTION: ${card.frontText}
                ANSWER: ${card.backText}
                
                Do NOT repeat or rephrase the question and answer. Instead, write a short 2-3 sentence 
                "deeper context" that answers a follow-up curious question like:
                - WHY is this true?
                - HOW does this work at a deeper level?
                - WHAT makes this concept important or interesting?
                - How does this connect to the bigger picture?
                
                Write it as if you're a curious teacher sparking the student's interest. 
                Keep it under 60 words. No bullet points. Plain prose only.
            """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val context = response.text?.trim() ?: "Could not generate context."

                withContext(Dispatchers.Main) {
                    val updatedCard = card.copy(contextText = context)
                    parsedFlashcards[index] = updatedCard
                    view.refreshFlashcardsList(parsedFlashcards)
                    view.showMessage("Context generated!")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.showMessage("Failed to generate context. Check connection.")
                }
            }
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
        if (index in parsedFlashcards.indices) {
            view.showEditCardDialog(index, parsedFlashcards[index])
        }
    }

    override fun onEditCardSaved(index: Int, newFront: String, newBack: String, newContext: String?) {
        if (index in parsedFlashcards.indices) {
            parsedFlashcards[index] = parsedFlashcards[index].copy(
                frontText = newFront,
                backText = newBack,
                contextText = newContext
            )
            view.refreshFlashcardsList(parsedFlashcards)
            view.showMessage("Card updated!")
        }
    }

    override fun onDeleteCardClicked(index: Int) {
        if (index in parsedFlashcards.indices) {
            parsedFlashcards.removeAt(index)
            view.refreshFlashcardsList(parsedFlashcards)
            view.showMessage("Card deleted.")
        }
    }
}
