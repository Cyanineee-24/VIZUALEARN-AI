package com.myApp.vizualearnfinal.screens.step3

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.myApp.vizualearnfinal.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Step3Presenter(
    private val view: Step3Contract.View,
    private val model: Step3Model
) : Step3Contract.Presenter {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
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

    override fun startGeneratingSimulation(
        notes: String,
        type: String,
        inputMethod: String,
        imageUris: List<String>,
        pdfUri: String?
    ) {
        Log.d("GEMINI_DEBUG", "Starting generation. Type: $type, Method: $inputMethod")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Build the base text instructions - UPGRADED TO NOTEBOOK LM "BRAIN"
                val baseInstructions = if (type == "mindmap") {
                    """
                    Extract key concepts from the provided content to create a highly organized Mind Map. 
                    Act as an expert taxonomist. Organize the information strictly into:
                    1. A single Core Topic
                    2. Major Branches (Sub-topics)
                    3. Leaves (Supporting details)
                    
                    Return ONLY a valid JSON array of objects. Each object MUST have exactly these keys:
                    - "id": A short, unique string identifier (e.g., "node_1").
                    - "parentId": The id of the parent node this belongs to. 
                    - "title": A 1-to-3 word title.
                    - "description": A 1-sentence explanation.
                    
                    CRITICAL RULES:
                    - The main central topic MUST have the exact "id": "root", and its "parentId" MUST be "null".
                    - Every other node MUST have a "parentId" that exactly matches an "id" of another node.
                    - Do not exceed 3 levels of depth to avoid clutter.
                    - Return raw JSON only, no markdown formatting.
                    """.trimIndent()
                } else {
                    "Read the provided content and create high-quality educational flashcards. Return ONLY a valid JSON array of objects, where each object has a 'frontText' (string) and 'backText' (string). No markdown, no introduction, just the raw JSON array."
                }

                val imageBlobs = mutableListOf<ByteArray>()
                var pdfBlob: ByteArray? = null

                if (inputMethod == "IMAGE" && imageUris.isNotEmpty()) {
                    for (uriStr in imageUris) {
                        model.readFileData(uriStr)?.let { imageBlobs.add(it) }
                    }
                } else if (inputMethod == "PDF" && pdfUri != null) {
                    pdfBlob = model.readFileData(pdfUri)
                    if (pdfBlob == null) throw Exception("Could not read PDF file.")
                }

                val inputContent = content {
                    if (inputMethod == "IMAGE" && imageBlobs.isNotEmpty()) {
                        for (data in imageBlobs) {
                            blob("image/jpeg", data)
                        }
                        text("Analyze the text found within these images. \n$baseInstructions")
                    }
                    else if (inputMethod == "PDF" && pdfBlob != null) {
                        blob("application/pdf", pdfBlob)
                        text("Analyze the text found within this PDF document. \n$baseInstructions")
                    }
                    else {
                        text("Notes: $notes\n\n$baseInstructions")
                    }
                }

                val response = generativeModel.generateContent(inputContent)
                val rawText = response.text
                Log.d("GEMINI_DEBUG", "Raw Response: $rawText")

                generatedJsonResult = rawText?.replace("```json", "")
                    ?.replace("```", "")
                    ?.trim() ?: "[]"

                withContext(Dispatchers.Main) {
                    if (generatedJsonResult == "[]" || generatedJsonResult.isEmpty()) {
                        view.showError("Gemini returned no data. Try a clearer image/document.")
                    } else {
                        view.enableContinueButton()
                    }
                }

            } catch (e: Exception) {
                Log.e("GEMINI_DEBUG", "ERROR DURING GENERATION: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    view.showError("Error generating cards: ${e.localizedMessage}")
                }
            }
        }
    }

    override fun onContinueClicked(setId: Int, type: String) {
        view.navigateToStep4(setId, type, generatedJsonResult)
    }
}