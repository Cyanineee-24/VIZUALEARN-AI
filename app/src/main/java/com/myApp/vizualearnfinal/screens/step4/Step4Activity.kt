package com.myApp.vizualearnfinal.screens.step4

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.dashboard.DashboardActivity
import com.myApp.vizualearnfinal.utils.*
import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject


class Step4Activity : AppCompatActivity(), Step4Contract.View {

    private lateinit var presenter: Step4Contract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step4_result)

        val setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        val creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"
        val jsonResult = intent.getStringExtra("EXTRA_JSON_RESULT") ?: "[]"

        // Catch the Name!
        val itemName = intent.getStringExtra("EXTRA_ITEM_NAME") ?: "Untitled"

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = Step4Presenter(this, Step4Model(repository))

        // Pass itemName to the Presenter
        presenter.initializeView(setId, creationType, itemName, jsonResult)

        getTextView(R.id.textviewSave)?.setOnClickListener {
            presenter.onSaveClicked()
        }
        getImageView(R.id.imageviewBack)?.setOnClickListener {
            finish()
        }
    }

    override fun showFlashcardsUI(setName: String, cards: List<Flashcard>) {
        findViewById<LinearLayout>(R.id.linearlayoutMindMapVariant)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.linearlayoutFlashcardsVariant)?.visibility = View.VISIBLE

        findViewById<TextView>(R.id.textviewFlashcardSetName)?.text = setName
        findViewById<TextView>(R.id.textviewCardCountBadge)?.text = "${cards.size} Cards"

        val container = findViewById<LinearLayout>(R.id.linearlayoutDynamicCards)
        container?.removeAllViews()

        val inflater = LayoutInflater.from(this)

        for (index in cards.indices) {
            val card = cards[index]
            val itemView = inflater.inflate(R.layout.item_flashcard_preview, container, false)

            // Inject Text Data
            itemView.findViewById<TextView>(R.id.textviewBadgeNumber)?.text = "${index + 1}"
            itemView.findViewById<TextView>(R.id.textviewCardTitle)?.text = "CARD ${index + 1}"
            itemView.findViewById<TextView>(R.id.textviewCardFront)?.text = card.frontText
            itemView.findViewById<TextView>(R.id.textviewCardBack)?.text = card.backText

            // Bind the Presenter Actions
            itemView.findViewById<TextView>(R.id.textviewGenerateContext)?.setOnClickListener {
                presenter.onGenerateContextClicked(index)
            }
            itemView.findViewById<TextView>(R.id.textviewWriteManually)?.setOnClickListener {
                presenter.onManualContextClicked(index)
            }
            itemView.findViewById<ImageView>(R.id.imageviewEditCard)?.setOnClickListener {
                presenter.onEditCardClicked(index)
            }
            itemView.findViewById<ImageView>(R.id.imageviewDeleteCard)?.setOnClickListener {
                presenter.onDeleteCardClicked(index)
            }

            // Handle Context Text Visibility
            val contextText = card.contextText
            if (!contextText.isNullOrEmpty()) {
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.text = contextText
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.visibility = View.VISIBLE
                itemView.findViewById<LinearLayout>(R.id.linearlayoutContextPrompt)?.visibility = View.GONE
            } else {
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.visibility = View.GONE
                itemView.findViewById<LinearLayout>(R.id.linearlayoutContextPrompt)?.visibility = View.VISIBLE
            }

            // Force it onto the screen
            container?.addView(itemView)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun showMindMapUI(setName: String, nodes: List<MindMapNode>) {
        getLinearLayout(R.id.linearlayoutFlashcardsVariant)?.visibility = View.GONE
        getLinearLayout(R.id.linearlayoutMindMapVariant)?.visibility = View.VISIBLE

        val elementsArray = JSONArray()

        nodes.forEach { node ->
            // 1. Add the Node (Force the root node to use the User's Set Name)
            val nodeTitle = if (node.nodeId == "root") setName else node.title

            elementsArray.put(JSONObject().apply {
                put("data", JSONObject().apply {
                    put("id", node.nodeId)
                    put("label", nodeTitle)
                    put("description", node.description)
                })
            })

            // 2. Add the Edge (Connecting lines based on hierarchy!)
            if (node.parentId != "null" && node.parentId.isNotEmpty() && node.nodeId != "root") {
                elementsArray.put(JSONObject().apply {
                    put("data", JSONObject().apply {
                        put("id", "edge_${node.nodeId}")
                        put("source", node.parentId)
                        put("target", node.nodeId)
                    })
                })
            }
        }

        val jsonString = elementsArray.toString()
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "")

        val webView = findViewById<WebView>(R.id.webviewMindMapPreview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        webView.addJavascriptInterface(object : Any() {
            @android.webkit.JavascriptInterface
            fun onNodeTapped(title: String, description: String) {
                runOnUiThread {
                    getTextView(R.id.textviewNodeTitle)?.text = title
                    getTextView(R.id.textviewNodeDesc)?.text = description
                }
            }
        }, "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.evaluateJavascript("javascript:loadGraph('$jsonString')", null)
            }
        }
        webView.loadUrl("file:///android_asset/mindmap.html")
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun finishToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun refreshFlashcardsList(cards: List<Flashcard>) {
        // Just re-run the UI setup with the updated list
        showFlashcardsUI(getTextView(R.id.textviewFlashcardSetName)?.text.toString(), cards)
    }

    override fun showManualContextInput(index: Int) {
        // Future feature: Open an AlertDialog with an EditText here.
        // For now, let's just toast so we know the wire-up works.
        toast("Opening manual keyboard input for Card ${index + 1}")

        // Example of what would happen when they hit "Save" on that dialog:
        // presenter.onManualContextSaved(index, "User typed this context manually.")
    }

    override fun showEditCardDialog(index: Int, card: Flashcard) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_edit_card, null)
        bottomSheetDialog.setContentView(view)

        val edittextEditFront = view.findViewById<EditText>(R.id.edittextEditFront)
        val edittextEditBack = view.findViewById<EditText>(R.id.edittextEditBack)
        val edittextEditContext = view.findViewById<EditText>(R.id.edittextEditContext)
        val textviewSaveCardChanges = view.findViewById<TextView>(R.id.textviewSaveCardChanges)
        val textviewCancel = view.findViewById<TextView>(R.id.textviewCancel)

        edittextEditFront.setText(card.frontText)
        edittextEditBack.setText(card.backText)
        edittextEditContext.setText(card.contextText ?: "")

        textviewCancel.setOnClickListener { bottomSheetDialog.dismiss() }

        textviewSaveCardChanges.setOnClickListener {
            val newFront = edittextEditFront.text.toString().trim()
            val newBack = edittextEditBack.text.toString().trim()
            val newContext = edittextEditContext.text.toString().trim().takeIf { it.isNotEmpty() }
            if (newFront.isEmpty() || newBack.isEmpty()) {
                toast("Front and Back cannot be empty!")
                return@setOnClickListener
            }
            presenter.onManualContextSaved(index, newContext ?: "")
            // Also update front/back — add a new presenter method for this
            presenter.onEditCardSaved(index, newFront, newBack, newContext)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
}