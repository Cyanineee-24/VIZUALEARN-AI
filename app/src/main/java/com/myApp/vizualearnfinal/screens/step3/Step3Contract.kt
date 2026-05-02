package com.myApp.vizualearnfinal.screens.step3


class Step3Contract {
    interface View {
        fun displaySavingLocation(setName: String)
        fun enableContinueButton()
        fun navigateToStep4(setId: Int, type: String, generatedJsonResult: String)
    }

    interface Presenter {
        fun loadStudySetDetails(setId: Int)
        fun startGeneratingSimulation(notes: String, type: String)
        fun onContinueClicked(setId: Int, type: String)
    }
}