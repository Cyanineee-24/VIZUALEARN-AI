package com.myApp.vizualearnfinal.screens.viewset

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.*

class ViewSetActivity : AppCompatActivity(), ViewSetContract.View {

    private lateinit var presenter: ViewSetContract.Presenter
    private var setId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sets)

        setupUniversalFooter()

        // Assume we passed the Set ID from the Dashboard click!
        setId = intent.getIntExtra("EXTRA_SET_ID", 0)

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = ViewSetPresenter(this, ViewSetModel(repository))

        // Load all the data for this set
        presenter.loadSetData(setId)

        getImageView(R.id.imageviewBack)?.setOnClickListener { navigateBack() }

        // Ensure you wire up your universal footer here too!
        // setupUniversalFooter()
    }

    override fun displaySetHeader(setName: String, subtitle: String) {
        getTextView(R.id.textviewSetTitle)?.text = setName
        getTextView(R.id.textviewSetSubtitle)?.text = subtitle
    }

    override fun displayFlashcards(cards: List<Flashcard>) {
        val container = getLinearLayout(R.id.linearlayoutFlashcardSetsContainer)
        container?.removeAllViews()

        val inflater = LayoutInflater.from(this)
        for ((index, card) in cards.withIndex()) {
            // We get to reuse the layout from Step 4!
            val itemView = inflater.inflate(R.layout.item_flashcard_preview, container, false)

            itemView.getTextView(R.id.textviewBadgeNumber)?.text = "${index + 1}"
            itemView.getTextView(R.id.textviewCardTitle)?.text = "CARD ${index + 1}"
            itemView.getTextView(R.id.textviewCardFront)?.text = card.frontText
            itemView.getTextView(R.id.textviewCardBack)?.text = card.backText

            container?.addView(itemView)
        }
    }

    override fun displayMindMapNodes(nodes: List<MindMapNode>) {
        val container = getLinearLayout(R.id.linearlayoutMindmapsContainer)
        container?.removeAllViews()

        // For now, if there are nodes, we can just print their titles.
        // We will build the full visual canvas later!
        for (node in nodes) {
            val textView = android.widget.TextView(this).apply {
                text = "• ${node.title}: ${node.description}"
                setTextColor(android.graphics.Color.parseColor("#1A1A1A"))
                setPadding(0, 16, 0, 16)
            }
            container?.addView(textView)
        }
    }

    override fun navigateBack() {
        finish()
    }
}