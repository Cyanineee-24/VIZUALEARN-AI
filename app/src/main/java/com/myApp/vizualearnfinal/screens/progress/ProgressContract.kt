package com.myApp.vizualearnfinal.screens.progress

interface ProgressContract {
    interface View {
        fun updateStreakUI(currentStreak: Int, bestStreak: Int, totalDays: Int)
        fun navigateBack()
    }

    interface Presenter {
        fun loadProgressData()
    }
}