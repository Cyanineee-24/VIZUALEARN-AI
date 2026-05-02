package com.myApp.vizualearnfinal.screens.step2

class Step2Contract {
    interface View {
        fun displaySavingLocation(setName: String)
        fun showMessage(message: String)
        fun navigateToStep3()
    }
    interface Presenter {
        fun loadStudySetDetails(setId: Int)
        fun validateAndGenerate(itemName: String)
    }
}