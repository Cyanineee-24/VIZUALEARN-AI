package com.myApp.vizualearnfinal.screens.addset

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.* // Imports your extensions

class AddSetActivity : AppCompatActivity(), AddSetContract.View {

    private lateinit var presenter: AddSetContract.Presenter
    private var selectedSubject: String = "Biology"
    private var selectedIconResName: String = "ic_dna"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_set)

        // MVP Initialization
        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = AddSetPresenter(this, AddSetModel(repository))

        setupSubjectChips()
        setupIconPicker()

        // Using extensions for click listeners and fetching text
        getTextView(R.id.textviewSave)?.setOnClickListener {
            presenter.saveStudySet(
                name = getEditTextStringValue(R.id.edittextSetName),
                subject = selectedSubject,
                iconResName = selectedIconResName,
                description = getEditTextStringValue(R.id.edittextDescription)
            )
        }

        getImageView(R.id.imageviewBack)?.setOnClickListener {
            navigateBackToDashboard()
        }
    }

    private fun setupSubjectChips() {
        // listOfNotNull ignores any views that might be missing in XML
        val chips = listOfNotNull(
            getTextView(R.id.textviewChipBiology), getTextView(R.id.textviewChipArts),
            getTextView(R.id.textviewChipHistory), getTextView(R.id.textviewChipChemistry),
            getTextView(R.id.textviewChipPhysics), getTextView(R.id.textviewChipEnglish),
            getTextView(R.id.textviewChipOthers)
        )

        for (chip in chips) {
            chip.setOnClickListener { clickedView ->
                chips.forEach {
                    it.setBackgroundResource(R.drawable.bg_chip_unselected)
                    it.setTextColor(Color.parseColor("#666666"))
                }
                clickedView.setBackgroundResource(R.drawable.bg_chip_selected)
                (clickedView as TextView).setTextColor(Color.parseColor("#4A3B9C"))
                selectedSubject = clickedView.text.toString()
            }
        }
    }

    private fun setupIconPicker() {
        val icons = listOfNotNull(
            getImageView(R.id.imageviewIconDNA), getImageView(R.id.imageviewIconArt),
            getImageView(R.id.imageviewIconTombstone), getImageView(R.id.imageviewIconAtom),
            getImageView(R.id.imageviewIconChemistry), getImageView(R.id.imageviewIconBook),
            getImageView(R.id.imageviewIconLightbulb), getImageView(R.id.imageviewIconGrid)
        )

        for (icon in icons) {
            icon.setOnClickListener { clickedView ->
                icons.forEach { it.alpha = 0.4f }
                clickedView.alpha = 1.0f
                selectedIconResName = clickedView.tag.toString()
            }
        }
        icons.firstOrNull()?.performClick()
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun navigateBackToDashboard() {
        finish()
    }
}