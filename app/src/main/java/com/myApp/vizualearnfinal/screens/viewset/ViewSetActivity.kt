package com.myApp.vizualearnfinal.screens.viewset // Keep your actual package name

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.flashcardview.FlashCardViewActivity
import com.myApp.vizualearnfinal.screens.mindmapview.MindMapViewActivity
import com.myApp.vizualearnfinal.utils.ContainerAdapter
import com.myApp.vizualearnfinal.utils.ContainerType
import com.myApp.vizualearnfinal.utils.DeckItem
import kotlinx.coroutines.launch

class ViewSetActivity : AppCompatActivity() {

    private lateinit var flashcardAdapter: ContainerAdapter
    private lateinit var mindMapAdapter: ContainerAdapter
    private lateinit var repository: StudySetRepository

    private var currentSetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sets)

        // 1. Get the Set ID passed from the previous screen
        currentSetId = intent.getIntExtra("EXTRA_SET_ID", -1)
        if (currentSetId == -1) {
            Toast.makeText(this, "Error loading set", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. Initialize the Database Repository
        val dao = AppDatabase.getDatabase(this).studySetDao()
        repository = StudySetRepository(dao)

        // 3. Setup the UI and Adapters
        setupUI()
        setupAdapters()

        // 4. Fetch the Data!
        loadData()
    }

    private fun setupUI() {
        // Back Button
        findViewById<ImageView>(R.id.imageviewBack).setOnClickListener {
            finish()
        }

        // Add Flashcard Set Button
        findViewById<LinearLayout>(R.id.linearlayoutAddNewFlashCardSetButton).setOnClickListener {
            // TODO: Navigate back to Step 1/2 to generate more cards
            Toast.makeText(this, "Add Flashcard Set clicked", Toast.LENGTH_SHORT).show()
        }

        // Add Mind Map Button
        findViewById<LinearLayout>(R.id.linearlayoutAddNewMindMapButton).setOnClickListener {
            // TODO: Navigate back to Step 1/2 to generate a new mind map
            Toast.makeText(this, "Add Mind Map clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAdapters() {
        // Initialize Flashcard Adapter
        flashcardAdapter = ContainerAdapter(emptyList()) { id, type ->
            // FIRE THE INTENT TO OPEN THE DECK
            val intent = Intent(this, FlashCardViewActivity::class.java)
            intent.putExtra("EXTRA_DECK_ID", id)
            startActivity(intent)
        }
        findViewById<RecyclerView>(R.id.rvFlashcardDecks).adapter = flashcardAdapter

        // Initialize Mind Map Adapter
        mindMapAdapter = ContainerAdapter(emptyList()) { id, type ->
            // FIRE THE INTENT TO OPEN THE MIND MAP
            val intent = Intent(this, MindMapViewActivity::class.java)
            intent.putExtra("EXTRA_MAP_ID", id)
            startActivity(intent)
        }
        findViewById<RecyclerView>(R.id.rvMindMaps).adapter = mindMapAdapter
    }

    private fun loadData() {
        lifecycleScope.launch {
            // Fetch the parent Study Set to get the Title AND the Icon
            val studySet = repository.getSetById(currentSetId)
            findViewById<TextView>(R.id.textviewSetTitle).text = studySet?.setName ?: "Unknown Set"
            findViewById<TextView>(R.id.textviewSetSubtitle).text = "Items inside this set"

            // NEW: Get the integer ID of the parent's icon (Fallback to ic_book if null)
            val iconName = studySet?.iconResName ?: "ic_book"
            val dynamicIconId = resources.getIdentifier(iconName, "drawable", packageName)

            // --- FETCH FLASHCARD DECKS ---
            val dbDecks = repository.getDecksForSet(currentSetId)
            val flashcardItems = dbDecks.map { deck ->
                val cards = repository.getFlashcardsForDeck(deck.id)
                DeckItem(
                    id = deck.id,
                    title = deck.deckName,
                    subtitle = "${cards.size} Cards",
                    progress = deck.progress,
                    type = ContainerType.FLASHCARD,
                    iconResId = dynamicIconId // <--- PASSING THE ICON!
                )
            }
            flashcardAdapter.updateData(flashcardItems)

            // --- FETCH MIND MAPS ---
            val dbMaps = repository.getMindMapsForSet(currentSetId)
            val mindMapItems = dbMaps.map { map ->
                val nodes = repository.getNodesForMap(map.id)
                DeckItem(
                    id = map.id,
                    title = map.mapName,
                    subtitle = "${nodes.size} nodes in total",
                    progress = 0,
                    type = ContainerType.MIND_MAP,
                    iconResId = dynamicIconId // <--- PASSING THE ICON!
                )
            }
            mindMapAdapter.updateData(mindMapItems)
        }
    }
}