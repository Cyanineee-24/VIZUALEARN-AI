package com.myApp.vizualearnfinal.screens.step2


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Step2Presenter(
    val view: Step2Contract.View,
    val model: Step2Model
) : Step2Contract.Presenter {

    override fun loadStudySetDetails(setId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val studySet = model.getStudySet(setId)
            if (studySet != null) {
                view.displaySavingLocation(studySet.setName)
            }
        }
    }

    override fun validateAndGenerate(itemName: String) {
        if (itemName.trim().isEmpty()) {
            view.showMessage("Please provide a name before generating.")
        } else {
            view.navigateToStep3()
        }
    }
}