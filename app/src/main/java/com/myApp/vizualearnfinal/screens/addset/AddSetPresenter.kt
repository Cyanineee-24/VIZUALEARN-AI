package com.myApp.vizualearnfinal.screens.addset

import com.myApp.vizualearnfinal.data.model.StudySet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddSetPresenter(
    val view: AddSetContract.View,
    val model: AddSetModel
): AddSetContract.Presenter {
    override fun saveStudySet(
        name: String,
        subject: String,
        iconResName: String,
        description: String
    ) {
        if(name.trim().isNotEmpty() && subject.trim().isNotEmpty()) {
            val newSet = StudySet(
                setName = name.trim(),
                subject = subject,
                iconResName = iconResName,
                description = description.trim()
            )

            // Save to database on background thread
            CoroutineScope(Dispatchers.Main).launch {
                model.insertSet(newSet)
                view.showMessage("Study Set Saved!")
                view.navigateBackToDashboard()
            }
        } else {
            view.showMessage("Please enter a name and subject.")
        }
    }
}