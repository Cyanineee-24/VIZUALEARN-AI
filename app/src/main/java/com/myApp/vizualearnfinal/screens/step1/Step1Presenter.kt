package com.myApp.vizualearnfinal.screens.step1

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Step1Presenter(
    val view: Step1Contract.View,
    val model: Step1Model
) : Step1Contract.Presenter {

    private var selectedMethod: String = ""

    override fun loadStudySetDetails(setId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val studySet = model.getStudySet(setId)
            if (studySet != null) {
                view.displaySavingLocation(studySet.setName)
            }
        }
    }

    override fun onInputMethodSelected(method: String) {
        selectedMethod = method
        // Tell the View to update the radio buttons
        view.updateRadioSelection(method)
    }

    override fun onContinueClicked(setId: Int, type: String) {
        if (selectedMethod.isEmpty()) {
            view.showMessage("Please select an input method first.")
        } else {
            // Pass the data forward to Step 2
            view.navigateToStep2(setId, type, selectedMethod)
        }
    }
}