package com.myApp.vizualearnfinal.screens.progress

class ProgressPresenter(
    private val view: ProgressContract.View,
    private val model: ProgressModel
) : ProgressContract.Presenter {

    override fun loadProgressData() {
        // Run our real-time date check!
        // This will only increment if the date actually changed since last time.
        model.recordDailyLogin()

        val current = model.getCurrentStreak()
        val best = model.getBestStreak()
        val total = model.getTotalDays()

        view.updateStreakUI(current, best, total)
    }
}