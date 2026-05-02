package com.myApp.vizualearnfinal.screens.step3

import android.util.Log // Import for Logcat
import com.google.ai.client.generativeai.GenerativeModel
import com.myApp.vizualearnfinal.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Step3Presenter(
    val view: Step3Contract.View,
    val model: Step3Model
) : Step3Contract.Presenter {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.GEMINI_API_KEY
    )

    private var generatedJsonResult: String = ""

    override fun loadStudySetDetails(setId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val studySet = model.getStudySet(setId)
            if (studySet != null) {
                view.displaySavingLocation(studySet.setName)
            }
        }
    }

    override fun startGeneratingSimulation(notes: String, type: String) {
        // Log that we are starting
        Log.d("GEMINI_DEBUG", "Starting generation for type: $type")

        CoroutineScope(Dispatchers.IO).launch { // Use IO for network calls
            try {
                val prompt = if (type == "mindmap") {
                    "Read these notes and extract key concepts for a Mind Map. Return ONLY a valid JSON array of objects, where each object has a 'title' (string) and 'description' (string). No markdown, no introduction, just the raw JSON array. Notes: $notes"
                } else {
                    "Read these notes and create high-quality flashcards. Return ONLY a valid JSON array of objects, where each object has a 'frontText' (string) and 'backText' (string). No markdown, no introduction, just the raw JSON array. Notes: $notes"
                }

                Log.d("GEMINI_DEBUG", "Sending prompt: $prompt")

                // 2. Call Gemini
                val response = generativeModel.generateContent(prompt)
                val rawText = response.text

                Log.d("GEMINI_DEBUG", "Raw Response: $rawText")

                // 3. Clean up the response
                generatedJsonResult = rawText?.replace("```json", "")
                    ?.replace("```", "")
                    ?.trim() ?: "[]"

                Log.d("GEMINI_DEBUG", "Cleaned JSON: $generatedJsonResult")

                // 4. Update UI on Main Thread
                withContext(Dispatchers.Main) {
                    if (generatedJsonResult == "[]" || generatedJsonResult.isEmpty()) {
                        Log.e("GEMINI_DEBUG", "Resulting JSON was empty")
                        // view.showError("Gemini returned no data")
                    } else {
                        Log.d("GEMINI_DEBUG", "Generation Success! Enabling button.")
                        view.enableContinueButton()
                    }
                }

            } catch (e: Exception) {
                Log.e("GEMINI_DEBUG", "ERROR DURING GENERATION: ${e.message}", e)

                withContext(Dispatchers.Main) {
                    // This is crucial: if it fails, tell the view so it can stop
                    // the loading spinner and show an error toast.
                    // view.showError("Connection error: ${e.localizedMessage}")
                }
            }
        }
    }

    override fun onContinueClicked(setId: Int, type: String) {
        view.navigateToStep4(setId, type, generatedJsonResult)
    }
}