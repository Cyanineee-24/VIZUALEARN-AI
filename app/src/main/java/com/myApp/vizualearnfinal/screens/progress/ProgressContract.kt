package com.myApp.vizualearnfinal.screens.progress

interface ProgressContract {
    interface View {
        // UPDATED: Added monthDays and missedDays to the signature
        fun updateStreakUI(
            currentStreak: Int,
            bestStreak: Int,
            totalDays: Int,
            monthDays: Int,
            missedDays: Int,
            nextMilestone: Int,
            daysToGo: Int,
            milestoneProgress: Int
        )
        fun updateMasteryUI(overallPercent: Int, subjects: List<ProgressModel.SubjectMastery>)
        fun navigateBack()
    }

    interface Presenter {
        fun loadProgressData()
    }
}