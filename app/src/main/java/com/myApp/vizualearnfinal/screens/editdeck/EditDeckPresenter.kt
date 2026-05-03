package com.myApp.vizualearnfinal.screens.editdeck

import com.google.ai.client.generativeai.GenerativeModel
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditDeckPresenter(
    private val view: EditDeckContract.View,
    private val model: EditDeckModel
) : EditDeckContract.Presenter {

    private var currentCards = mutableListOf<Flashcard>()
    private var currentDeckId: Int = -1

    // Initialize Gemini AI
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = Constants.GEMINI_API_KEY
    )

    override fun loadDeck(deckId: Int) {
        currentDeckId = deckId
        CoroutineScope(Dispatchers.Main).launch {
            currentCards.clear()
            currentCards.addAll(model.getCardsForDeck(deckId))
            view.showCards(currentCards)
        }
    }

    override fun onEditClicked(index: Int) {
        if (index in currentCards.indices) {
            view.showEditCardDialog(index, currentCards[index])
        }
    }

    override fun onDeleteClicked(index: Int) {
        if (index in currentCards.indices) {
            val cardToDelete = currentCards[index]
            CoroutineScope(Dispatchers.Main).launch {
                model.deleteFlashcard(cardToDelete)

                // NEW: Remove from learned progress and sync math!
                model.removeCardFromLearned(currentDeckId, cardToDelete.id)
                model.syncDeckProgress(currentDeckId)

                currentCards.removeAt(index)
                view.showCards(currentCards)
                view.showMessage("Card deleted.")
            }
        }
    }

    override fun onEditCardSaved(index: Int, newFront: String, newBack: String, newContext: String?) {
        if (index in currentCards.indices) {
            val updatedCard = currentCards[index].copy(
                frontText = newFront,
                backText = newBack,
                contextText = newContext
            )
            CoroutineScope(Dispatchers.Main).launch {
                model.updateFlashcard(updatedCard) // Save to DB
                currentCards[index] = updatedCard
                view.showCards(currentCards)
                view.showMessage("Card updated successfully!")
            }
        }
    }

    // --- AI CONTEXT HELPERS ---
    private suspend fun fetchContextFromGemini(front: String, back: String): String {
        val prompt = """
            You are an expert educator helping a student deeply understand a concept.
            The student has this flashcard:
            QUESTION: $front
            ANSWER: $back
            
            Do NOT repeat or rephrase the question and answer. Instead, write a short 2-3 sentence 
            "deeper context" explaining WHY this is true or HOW it works.
            Keep it under 60 words. No bullet points. Plain prose only.
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        return response.text?.trim() ?: "Could not generate context."
    }

    override fun onGenerateContextClicked(index: Int) {
        if (index !in currentCards.indices) return
        val card = currentCards[index]
        view.showMessage("Gemini is analyzing Card ${index + 1}...")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val context = fetchContextFromGemini(card.frontText, card.backText)
                withContext(Dispatchers.Main) {
                    val updatedCard = card.copy(contextText = context)
                    model.updateFlashcard(updatedCard) // Save to DB immediately
                    currentCards[index] = updatedCard
                    view.showCards(currentCards)
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
        if (index in currentCards.indices) {
            view.showEditCardDialog(index, currentCards[index])
        }
    }

    override fun generateContextForText(front: String, back: String, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val context = fetchContextFromGemini(front, back)
                withContext(Dispatchers.Main) { callback(context) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback("Failed to generate context. Check connection.") }
            }
        }
    }

    // --- ADD CARD LOGIC ---
    override fun onAddCardClicked() {
        view.showEditCardDialog(-1, null)
    }

    override fun onAddCardSaved(front: String, back: String, contextText: String?) {
        val newCard = Flashcard(
            deckId = currentDeckId,
            frontText = front,
            backText = back,
            contextText = contextText
        )

        CoroutineScope(Dispatchers.Main).launch {
            model.insertFlashcard(newCard)

            // NEW: Adding a card lowers your overall percentage, so sync it!
            model.syncDeckProgress(currentDeckId)

            loadDeck(currentDeckId)
            view.showMessage("New card added!")
        }
    }
}