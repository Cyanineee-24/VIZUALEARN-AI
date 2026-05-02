package com.myApp.vizualearnfinal.screens.selecttargetset

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.addset.AddSetActivity
import com.myApp.vizualearnfinal.screens.step1.Step1Activity
import com.myApp.vizualearnfinal.utils.*

class SelectTargetSetActivity : AppCompatActivity(), SelectTargetSetContract.View {

    private lateinit var presenter: SelectTargetSetContract.Presenter
    private var setsContainer: LinearLayout? = null
    private var addSetPromptLayout: LinearLayout? = null
    private var creationType: String = "flashcard"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_target_set)

        creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"
        getTextView(R.id.textviewScreenTitle)?.text = if (creationType == "mindmap") "Save Mind Map to..." else "Save Flashcard to..."

        val dao = AppDatabase.getDatabase(this).studySetDao()
        presenter = SelectTargetSetPresenter(this, SelectTargetSetModel(StudySetRepository(dao)))

        setsContainer = getLinearLayout(R.id.linearlayoutStudySetsContainer)
        addSetPromptLayout = getLinearLayout(R.id.linearlayoutAddSetPrompt)

        getImageView(R.id.imageviewAddSetButton)?.setOnClickListener { start(AddSetActivity::class.java) }
        
        // Implementing the Cancel button (TextView in XML)
        getTextView(R.id.textviewCancel)?.setOnClickListener { 
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadSets()
    }

    override fun displayStudySets(sets: List<StudySet>) {
        setsContainer?.removeAllViews()
        addSetPromptLayout?.let { setsContainer?.addView(it) }

        val inflater = LayoutInflater.from(this)
        for (set in sets) {
            val itemView = inflater.inflate(R.layout.item_study_set, setsContainer, false)

            itemView.getImageView(R.id.imageviewItemIcon)?.setImageResource(resources.getIdentifier(set.iconResName, "drawable", packageName))
            itemView.getTextView(R.id.textviewItemTitle)?.text = set.setName
            itemView.getTextView(R.id.textviewItemSubTitle)?.text = set.subject

            itemView.setOnClickListener { presenter.onSetSelected(set.id, creationType) }
            setsContainer?.addView(itemView, (setsContainer?.childCount ?: 1) - 1)
        }
    }

    override fun showEmptyState(show: Boolean) {
        addSetPromptLayout?.visibility = View.VISIBLE
    }

    override fun navigateToStep1(setId: Int, type: String) {
        val intent = Intent(this, Step1Activity::class.java)
        intent.putExtra("EXTRA_SET_ID", setId)
        intent.putExtra("EXTRA_TYPE", type)
        startActivity(intent)
    }
}
