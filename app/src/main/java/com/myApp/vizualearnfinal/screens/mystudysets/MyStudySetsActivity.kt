package com.myApp.vizualearnfinal.screens.mystudysets

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.addset.AddSetActivity
import com.myApp.vizualearnfinal.screens.viewset.ViewSetActivity
import com.myApp.vizualearnfinal.utils.*

class MyStudySetsActivity : AppCompatActivity(), MyStudySetsContract.View {

    private lateinit var presenter: MyStudySetsContract.Presenter
    private var container: LinearLayout? = null
    private var emptyStatePrompt: View? = null
    private val filterChips = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_study_sets)

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = MyStudySetsPresenter(this, MyStudySetsModel(repository))

        container = getLinearLayout(R.id.dynamic_study_sets_container)
        emptyStatePrompt = getLinearLayout(R.id.btn_create_new_set)

        // Find all the filter pills dynamically by looping through the HorizontalScrollView's child
        val pillContainer = findViewById<View>(R.id.pill_all)?.parent as? ViewGroup
        if (pillContainer != null) {
            for (i in 0 until pillContainer.childCount) {
                val view = pillContainer.getChildAt(i)
                if (view is TextView) {
                    filterChips.add(view)
                    view.setOnClickListener {
                        presenter.filterSetsBySubject(view.text.toString())
                    }
                }
            }
        }

        // Handle Add Set Button
        emptyStatePrompt?.setOnClickListener {
            startActivity(Intent(this, AddSetActivity::class.java))
        }

        // Wire up the Universal Footer!
        setupUniversalFooter()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadSets()
    }

    override fun displayStudySets(sets: List<StudySet>) {
        container?.removeAllViews()
        val inflater = LayoutInflater.from(this)

        for (set in sets) {
            val itemView = inflater.inflate(R.layout.item_study_set, container, false)

            itemView.getImageView(R.id.imageviewItemIcon)?.let { iv ->
                val iconResId = resources.getIdentifier(set.iconResName, "drawable", packageName)
                if (iconResId != 0) iv.setImageResource(iconResId)
            }

            itemView.getTextView(R.id.textviewItemTitle)?.text = set.setName
            itemView.getTextView(R.id.textviewItemSubTitle)?.text = "${set.cardCount} cards set - ${set.mindMapCount} mind map"
            itemView.getTextView(R.id.textviewItemSubjectTag)?.text = set.subject

            // Make them clickable!
            itemView.setOnClickListener {
                val intent = Intent(this, ViewSetActivity::class.java).apply {
                    putExtra("EXTRA_SET_ID", set.id)
                }
                startActivity(intent)
            }

            container?.addView(itemView)
        }
    }

    override fun showEmptyState(show: Boolean) {
        emptyStatePrompt?.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun updateFilterUI(selectedSubject: String) {
        // Change colors to show which pill is active
        for (chip in filterChips) {
            if (chip.text.toString().equals(selectedSubject, ignoreCase = true)) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.parseColor("#7E57C2"))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(Color.parseColor("#666666"))
            }
        }
    }

    override fun updateHeaderCount(count: Int) {
        // Find the subtitle in the header (you didn't give it an ID in XML, so we find it by navigating)
        val headerLayout = findViewById<LinearLayout>(R.id.sticky_header)?.getChildAt(0) as? LinearLayout
        val countTextView = headerLayout?.getChildAt(1) as? TextView
        countTextView?.text = "$count Sets Created"
    }
}