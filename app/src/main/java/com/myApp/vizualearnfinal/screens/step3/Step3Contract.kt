package com.myApp.vizualearnfinal.screens.step3

interface Step3Contract {
    interface View {
        fun displaySavingLocation(setName: String)
        fun enableContinueButton()
        fun navigateToStep4(setId: Int, type: String, generatedJsonResult: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun loadStudySetDetails(setId: Int)
        fun startGeneratingSimulation(
            notes: String,
            type: String,
            inputMethod: String,
            imageUris: List<String>,
            pdfUri: String?
        )
        fun onContinueClicked(setId: Int, type: String)
    }
}