package com.myApp.vizualearnfinal.screens.progress

interface ProgressContract {
    interface View {
        // UPDATED: Now accepts the calculated milestone numbers
        fun updateStreakUI(currentStreak: Int, bestStreak: Int, totalDays: Int, nextMilestone: Int, daysToGo: Int, milestoneProgress: Int)
        fun updateMasteryUI(overallPercent: Int, subjects: List<ProgressModel.SubjectMastery>)
        fun navigateBack()
    }

    interface Presenter {
        fun loadProgressData()
    }
}