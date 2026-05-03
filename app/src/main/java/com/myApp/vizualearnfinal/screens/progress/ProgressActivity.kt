package com.myApp.vizualearnfinal.screens.progress

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View as AndroidView // Alias to avoid conflict with ProgressContract.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.getImageView
import com.myApp.vizualearnfinal.utils.getLinearLayout
import com.myApp.vizualearnfinal.utils.getTextView
import com.myApp.vizualearnfinal.utils.setupUniversalFooter
import java.time.LocalDate

class ProgressActivity : AppCompatActivity(), ProgressContract.View {

    private lateinit var presenter: ProgressContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        // Initialize Repository and Model (Model now requires a repository)
        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        val model = ProgressModel(this, repository)
        
        presenter = ProgressPresenter(this, model)

        // Wire up the bottom navigation!
        setupUniversalFooter()
    }

    override fun onResume() {
        super.onResume()
        // Load the data whenever the screen becomes visible
        presenter.loadProgressData()
    }

    // UPDATED: Now accepts monthDays and missedDays
    override fun updateStreakUI(
        currentStreak: Int,
        bestStreak: Int,
        totalDays: Int,
        monthDays: Int,
        missedDays: Int,
        nextMilestone: Int,
        daysToGo: Int,
        milestoneProgress: Int
    ) {
        getTextView(R.id.textviewStreakCount)?.text = currentStreak.toString()
        getTextView(R.id.textviewBestNumber)?.text = bestStreak.toString()
        getTextView(R.id.textviewStatTotalNumber)?.text = totalDays.toString()

        // THE FIX: Wire up the dynamic numbers! No more fake '3' missed days!
        getTextView(R.id.textviewStatMonthNumber)?.text = monthDays.toString()
        getTextView(R.id.textviewStatMissedNumber)?.text = missedDays.toString()

        getTextView(R.id.textviewMilestoneGoalText)?.text = "$nextMilestone days — $daysToGo to go"

        val progressFill = findViewById<AndroidView>(R.id.viewMilestoneFill)
        val layoutParams = progressFill?.layoutParams as? LinearLayout.LayoutParams
        layoutParams?.let {
            it.weight = milestoneProgress.toFloat()
            progressFill.layoutParams = it
        }

        // --- THE REAL-TIME CALENDAR & DATE UPDATE ---
        val today = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate.now()
        } else null

        val todayValue = today?.dayOfWeek?.value ?: 1
        val startOfWeekDate = today?.minusDays((todayValue - 1).toLong())
        val startDayOfStreakThisWeek = todayValue - currentStreak + 1

        val dayLayouts = arrayOf(R.id.linearlayoutDayMon, R.id.linearlayoutDayTue, R.id.linearlayoutDayWed, R.id.linearlayoutDayThu, R.id.linearlayoutDayFri, R.id.linearlayoutDaySat, R.id.linearlayoutDaySun)
        val textViews = arrayOf(R.id.textviewDayMon, R.id.textviewDayTue, R.id.textviewDayWed, R.id.textviewDayThu, R.id.textviewDayFri, R.id.textviewDaySat, R.id.textviewDaySun)

        for (i in 0..6) {
            val dayNumber = i + 1
            val layout = getLinearLayout(dayLayouts[i])
            val textView = getTextView(textViews[i])
            val flameIcon = layout?.getChildAt(0) as? android.widget.ImageView

            if (startOfWeekDate != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val dateForSlot = startOfWeekDate.plusDays(i.toLong())
                val monthStr = dateForSlot.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                textView?.text = "$monthStr ${dateForSlot.dayOfMonth}"
            }

            if (dayNumber in startDayOfStreakThisWeek..todayValue) {
                flameIcon?.setBackgroundResource(R.drawable.bg_streak_day_active)
                flameIcon?.setImageResource(R.drawable.ft_streak)
                textView?.setTextColor(Color.WHITE)
            } else {
                flameIcon?.setBackgroundResource(R.drawable.bg_streak_best_box)
                flameIcon?.setImageDrawable(null)
                textView?.setTextColor(Color.parseColor("#555566"))
            }
        }
    }

    override fun navigateBack() {
        finish()
    }



    override fun updateMasteryUI(overallPercent: Int, subjects: List<ProgressModel.SubjectMastery>) {
        // 1. Update the Donut Chart!
        val progressBarMastery = findViewById<ProgressBar>(R.id.progressbarMastery)
        val textviewMasteryPercent = getTextView(R.id.textviewMasteryPercent)

        progressBarMastery?.progress = overallPercent
        textviewMasteryPercent?.text = "$overallPercent%"

        // 2. Clear out the old content and populate the dynamic list
        val container = findViewById<LinearLayout>(R.id.linearlayoutBySubjectCard)

        // We want to keep the "By Subject" title, so we remove all views EXCEPT the first one (index 0)
        if (container != null && container.childCount > 1) {
            container.removeViews(1, container.childCount - 1)
        }

        val inflater = LayoutInflater.from(this)

        for (subject in subjects) {
            val itemView = inflater.inflate(R.layout.item_mastery_subject, container, false)

            // Setup Text
            itemView.getTextView(R.id.textviewSubjectTitle)?.text = subject.subjectName
            itemView.getTextView(R.id.textviewSubjectCards)?.text = "${subject.learned} / ${subject.total} cards"
            itemView.getTextView(R.id.textviewSubjectPercent)?.text = "${subject.percent}%"

            // Setup Icon
            val iconResId = resources.getIdentifier(subject.iconResName, "drawable", packageName)
            if (iconResId != 0) {
                itemView.getImageView(R.id.imageviewIcon)?.setImageResource(iconResId)
            }

            // Dynamic Colors based on subject
            val themeColor = when (subject.subjectName.lowercase()) {
                "biology" -> "#00897B"
                "world history", "history" -> "#1E88E5"
                "calculus ii", "math" -> "#FF8F00"
                "data structures", "computer science" -> "#8E24AA"
                "quantum physics", "physics" -> "#D81B60"
                else -> "#7E57C2"
            }

            // Apply the color to the percentage text and the progress bar fill
            itemView.getTextView(R.id.textviewSubjectPercent)?.setTextColor(Color.parseColor(themeColor))
            
            // Explicitly use the aliased AndroidView here to resolve the conflict
            val progressFill = itemView.findViewById<AndroidView>(R.id.viewProgressFill)
            progressFill?.setBackgroundColor(Color.parseColor(themeColor))

            // Set the width of the progress bar based on the percentage
            val layoutParams = progressFill?.layoutParams as? LinearLayout.LayoutParams
            layoutParams?.let {
                it.weight = subject.percent.toFloat()
                progressFill.layoutParams = it
            }

            // Add it to the screen!
            container?.addView(itemView)
        }
    }
}
