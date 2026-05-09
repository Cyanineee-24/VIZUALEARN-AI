package com.myApp.vizualearnfinal.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.application.CustomApplication
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.addset.AddSetActivity
import com.myApp.vizualearnfinal.screens.selecttargetset.SelectTargetSetActivity
import com.myApp.vizualearnfinal.utils.*

class DashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var presenter: DashboardContract.Presenter
    private var studySetsContainer: LinearLayout? = null
    private var addSetPromptLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setupUniversalFooter()

        // MVP Initialization
        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        val app = application as CustomApplication
        presenter = DashboardPresenter(this, DashboardModel(repository, app))

        // UI Components
        studySetsContainer = getLinearLayout(R.id.linearlayoutStudySetsContainer)
        addSetPromptLayout = getLinearLayout(R.id.linearlayoutAddSetPrompt)

        // Load User Data (Name and Streak)
        presenter.loadUserData()

        // Delegate Clicks to Presenter
        getTextView(R.id.textviewCreateMindMap)?.setOnClickListener {
            presenter.onCreateMindMapClicked()
        }

        getTextView(R.id.textviewCreateFlashCards)?.setOnClickListener {
            presenter.onCreateFlashCardsClicked()
        }

        getLinearLayout(R.id.linearlayoutAddSetPrompt)?.setOnClickListener {
            presenter.onAddSetClicked()
        }

        // Setup Bottom Navigation
        setupUniversalFooter()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadSets() // Reload sets whenever the user comes back to the dashboard
    }

    override fun displayUserData(userName: String, streakDays: Int) {
        // Updates the top header with real login data
        getTextView(R.id.textviewUsername)?.text = userName
        getTextView(R.id.textviewUserStreak)?.text = "$streakDays - day streak!"
    }

    override fun displayStudySets(sets: List<StudySet>) {
        studySetsContainer?.removeAllViews()

        // Re-attach the dashed prompt button at the bottom of the container
        addSetPromptLayout?.let { studySetsContainer?.addView(it) }

        val inflater = LayoutInflater.from(this)

        for (set in sets) {
            val itemView = inflater.inflate(R.layout.item_study_set, studySetsContainer, false)

            val ivIcon = itemView.getImageView(R.id.imageviewItemIcon)
            val tvTitle = itemView.getTextView(R.id.textviewItemTitle)
            val tvSubtitle = itemView.getTextView(R.id.textviewItemSubTitle)
            val tvTag = itemView.getTextView(R.id.textviewItemSubjectTag)

            tvTitle?.text = set.setName
            tvSubtitle?.text = "${set.cardCount} cards set - ${set.mindMapCount} mind map"
            tvTag?.text = set.subject

            val iconResId = resources.getIdentifier(set.iconResName, "drawable", packageName)
            if (iconResId != 0) ivIcon?.setImageResource(iconResId)

            // ==========================================================
            // NEW: The Click Listener to open the specific Set
            // ==========================================================
            itemView.setOnClickListener {
                val intent = Intent(this, com.myApp.vizualearnfinal.screens.viewset.ViewSetActivity::class.java).apply {
                    putExtra("EXTRA_SET_ID", set.id)
                }
                startActivity(intent)
            }

            // Add the new card right above the dashed prompt
            val insertIndex = (studySetsContainer?.childCount ?: 1) - 1
            studySetsContainer?.addView(itemView, insertIndex)
        }
    }

    override fun showEmptyState(show: Boolean) {
        addSetPromptLayout?.visibility = View.VISIBLE
    }

    override fun navigateToSelectSet(creationType: String) {
        val intent = Intent(this, SelectTargetSetActivity::class.java).apply {
            putExtra("EXTRA_TYPE", creationType)
        }
        startActivity(intent)
    }

    override fun navigateToAddSet() {
        startActivity(Intent(this, AddSetActivity::class.java))
    }
}