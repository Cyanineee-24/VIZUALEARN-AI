package com.myApp.vizualearnfinal.screens.step1


class Step1Contract {
    interface View {
        fun displaySavingLocation(setName: String)
        fun showMessage(message: String)
        fun updateRadioSelection(selectedMethod: String)
        fun navigateToStep2(setId: Int, type: String, inputMethod: String)
    }

    interface Presenter {
        fun loadStudySetDetails(setId: Int)
        fun onInputMethodSelected(method: String)
        fun onContinueClicked(setId: Int, type: String)
    }
}