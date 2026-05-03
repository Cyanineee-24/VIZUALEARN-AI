package com.myApp.vizualearnfinal.screens.progress

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProgressPresenter(
    private val view: ProgressContract.View,
    private val model: ProgressModel
) : ProgressContract.Presenter {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadProgressData() {
        model.recordDailyLogin()
        val current = model.getCurrentStreak()
        val best = model.getBestStreak()
        val total = model.getTotalDays()

        // The Math!
        val nextMilestone = ((current / 10) + 1) * 10
        val daysToGo = nextMilestone - current
        val milestoneProgress = if (current % 10 == 0 && current > 0) 100 else ((current % 10) * 100) / 10

        // Pass EVERYTHING to the View
        view.updateStreakUI(current, best, total, nextMilestone, daysToGo, milestoneProgress)

        // Fetch Mastery Data and send to View
        CoroutineScope(Dispatchers.Main).launch {
            val (overallPercent, subjectList) = model.getMasteryData()
            view.updateMasteryUI(overallPercent, subjectList)
        }
    }
}