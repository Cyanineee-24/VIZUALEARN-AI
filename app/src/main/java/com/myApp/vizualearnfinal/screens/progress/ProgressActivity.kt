package com.myApp.vizualearnfinal.screens.progress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.setupUniversalFooter

class ProgressActivity : AppCompatActivity(), ProgressContract.View {

    private lateinit var presenter: ProgressContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        // Pass 'this' as the Context so SharedPreferences can access the phone's storage
        presenter = ProgressPresenter(this, ProgressModel(this))

        // Wire up the bottom navigation!
        setupUniversalFooter()
    }

    override fun onResume() {
        super.onResume()
        // Load the data whenever the screen becomes visible
        presenter.loadProgressData()
    }

    override fun updateStreakUI(currentStreak: Int, bestStreak: Int, totalDays: Int) {
        // Update the massive streak numbers
        getTextView(R.id.textviewStreakCount)?.text = currentStreak.toString()
        getTextView(R.id.textviewBestNumber)?.text = bestStreak.toString()

        // Update the smaller stat blocks at the bottom
        getTextView(R.id.textviewStatTotalNumber)?.text = totalDays.toString()

        // Let's just make the "This Month" stat equal to current streak for now
        getTextView(R.id.textviewStatMonthNumber)?.text = currentStreak.toString()
    }

    override fun navigateBack() {
        finish()
    }
}