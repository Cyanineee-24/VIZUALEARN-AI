package com.myApp.vizualearnfinal.screens.viewset

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.flashcardview.FlashCardViewActivity
import com.myApp.vizualearnfinal.screens.mindmapview.MindMapViewActivity
import com.myApp.vizualearnfinal.screens.step1.Step1Activity
import com.myApp.vizualearnfinal.utils.ContainerAdapter
import com.myApp.vizualearnfinal.utils.ContainerType
import com.myApp.vizualearnfinal.utils.DeckItem

class ViewSetActivity : AppCompatActivity(), ViewSetContract.View {

    private lateinit var presenter: ViewSetContract.Presenter

    // Store items so the click listener can look up the title by ID
    private val mindMapItems = mutableListOf<DeckItem>()
    private val flashcardItems = mutableListOf<DeckItem>()

    private lateinit var flashcardAdapter: ContainerAdapter
    private lateinit var mindMapAdapter: ContainerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sets)

        val setId = intent.getIntExtra("EXTRA_SET_ID", -1)
        if (setId == -1) {
            showError("Error loading set")
            finish()
            return
        }

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = ViewSetPresenter(this, ViewSetModel(this, repository))

        setupUI()
        setupAdapters()
        presenter.loadSetData(setId)
    }

    override fun onResume() {
        super.onResume()
        val setId = intent.getIntExtra("EXTRA_SET_ID", -1)
        if (setId != -1) presenter.loadSetData(setId)
    }

    private fun setupUI() {
        findViewById<ImageView>(R.id.imageviewBack).setOnClickListener { navigateBack() }

        findViewById<LinearLayout>(R.id.linearlayoutAddNewFlashCardSetButton)?.setOnClickListener {
            presenter.onAddFlashcardClicked()
        }

        findViewById<LinearLayout>(R.id.linearlayoutAddNewMindMapButton)?.setOnClickListener {
            presenter.onAddMindMapClicked()
        }
    }

    private fun setupAdapters() {
        flashcardAdapter = ContainerAdapter(emptyList()) { id, type ->
            val intent = Intent(this, FlashCardViewActivity::class.java)
            intent.putExtra("EXTRA_DECK_ID", id)
            startActivity(intent)
        }
        findViewById<RecyclerView>(R.id.rvFlashcardDecks).adapter = flashcardAdapter

        mindMapAdapter = ContainerAdapter(emptyList()) { id, type ->
            // FIX: Look up the title from our stored list so we pass the real name
            val mapTitle = mindMapItems.find { it.id == id }?.title ?: "Mind Map"
            val intent = Intent(this, MindMapViewActivity::class.java)
            intent.putExtra("EXTRA_MAP_ID", id)
            intent.putExtra("EXTRA_MAP_TITLE", mapTitle) // <-- THE FIX
            startActivity(intent)
        }
        findViewById<RecyclerView>(R.id.rvMindMaps).adapter = mindMapAdapter
    }

    override fun displaySetHeader(setName: String, subtitle: String) {
        findViewById<TextView>(R.id.textviewSetTitle).text = setName
        findViewById<TextView>(R.id.textviewSetSubtitle).text = subtitle
    }

    override fun displayDecks(flashcardDecks: List<DeckItem>, mindMapDecks: List<DeckItem>) {
        // FIX: Store copies so click listeners can look up titles
        flashcardItems.clear()
        flashcardItems.addAll(flashcardDecks)
        mindMapItems.clear()
        mindMapItems.addAll(mindMapDecks)

        flashcardAdapter.updateData(flashcardDecks)
        mindMapAdapter.updateData(mindMapDecks)
    }

    override fun getIconResourceId(iconName: String): Int {
        return resources.getIdentifier(iconName, "drawable", packageName)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateBack() {
        finish()
    }

    override fun navigateToStep1(setId: Int, type: String) {
        val intent = Intent(this, Step1Activity::class.java).apply {
            putExtra("EXTRA_SET_ID", setId)
            putExtra("EXTRA_TYPE", type)
        }
        startActivity(intent)
    }
}