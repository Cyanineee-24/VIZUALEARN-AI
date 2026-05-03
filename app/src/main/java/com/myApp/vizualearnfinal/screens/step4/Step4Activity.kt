package com.myApp.vizualearnfinal.screens.step4

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.model.Flashcard
import com.myApp.vizualearnfinal.data.model.MindMapNode
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.dashboard.DashboardActivity
import com.myApp.vizualearnfinal.utils.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream

class Step4Activity : AppCompatActivity(), Step4Contract.View {

    private lateinit var presenter: Step4Contract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step4_result)

        val setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        val creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"
        val jsonResult = intent.getStringExtra("EXTRA_JSON_RESULT") ?: "[]"
        val itemName = intent.getStringExtra("EXTRA_ITEM_NAME") ?: "Untitled"

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = Step4Presenter(this, Step4Model(repository))

        presenter.initializeView(setId, creationType, itemName, jsonResult)

        getTextView(R.id.textviewSave)?.setOnClickListener {
            presenter.onSaveClicked()
        }
        getImageView(R.id.imageviewBack)?.setOnClickListener {
            finish()
        }
        getTextView(R.id.textviewAddCard)?.setOnClickListener {
            presenter.onAddCardClicked()
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

            itemView.findViewById<TextView>(R.id.textviewBadgeNumber)?.text = "${index + 1}"
            itemView.findViewById<TextView>(R.id.textviewCardTitle)?.text = "CARD ${index + 1}"
            itemView.findViewById<TextView>(R.id.textviewCardFront)?.text = card.frontText
            itemView.findViewById<TextView>(R.id.textviewCardBack)?.text = card.backText

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

            val contextText = card.contextText
            if (!contextText.isNullOrEmpty()) {
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.text = contextText
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.visibility = View.VISIBLE
                itemView.findViewById<LinearLayout>(R.id.linearlayoutContextPrompt)?.visibility = View.GONE
            } else {
                itemView.findViewById<TextView>(R.id.textviewSavedContext)?.visibility = View.GONE
                itemView.findViewById<LinearLayout>(R.id.linearlayoutContextPrompt)?.visibility = View.VISIBLE
            }
            container?.addView(itemView)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun showMindMapUI(setName: String, nodes: List<MindMapNode>) {
        getLinearLayout(R.id.linearlayoutFlashcardsVariant)?.visibility = View.GONE
        getLinearLayout(R.id.linearlayoutMindMapVariant)?.visibility = View.VISIBLE

        val elementsArray = JSONArray()

        nodes.forEach { node ->
            // Let the node keep its real title!
            val nodeTitle = node.title

            elementsArray.put(JSONObject().apply {
                put("data", JSONObject().apply {
                    put("id", node.nodeId)
                    put("label", nodeTitle)
                    put("description", node.description)
                })
            })

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

        // BASE64 FIX PREVENTS HTML CRASH FROM LARGE PDF TEXTS
        val rawJsonString = elementsArray.toString()
        val base64Json = android.util.Base64.encodeToString(
            rawJsonString.toByteArray(Charsets.UTF_8),
            android.util.Base64.NO_WRAP
        )

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

            @android.webkit.JavascriptInterface
            fun saveImage(base64String: String) {
                val cleanBase64 = base64String.replace("data:image/png;base64,", "")
                val imageBytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)

                val filename = "MindMap_${System.currentTimeMillis()}.png"
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/VizuaLearn")
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                try {
                    uri?.let {
                        val outputStream: OutputStream? = resolver.openOutputStream(it)
                        outputStream?.use { stream -> stream.write(imageBytes) }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                            resolver.update(it, contentValues, null, null)
                        }
                        runOnUiThread { toast("Exported mind map to images!") }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { toast("Failed to save image.") }
                }
            }
        }, "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // LOAD USING BASE64 JAVASCRIPT FUNCTION
                webView.evaluateJavascript("javascript:loadGraphBase64('$base64Json')", null)
            }
        }
        webView.loadUrl("file:///android_asset/mindmap.html")

        // WIRE EXPORT BUTTON
        getLinearLayout(R.id.linearlayoutBtnExport)?.setOnClickListener {
            webView.evaluateJavascript("javascript:exportToAndroid()", null)
        }
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
        showFlashcardsUI(getTextView(R.id.textviewFlashcardSetName)?.text.toString(), cards)
    }

    override fun showManualContextInput(index: Int) {
        toast("Opening manual keyboard input for Card ${index + 1}")
    }

    override fun showEditCardDialog(index: Int, card: Flashcard?) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_edit_card, null)
        bottomSheetDialog.setContentView(view)

        val edittextEditFront = view.findViewById<EditText>(R.id.edittextEditFront)
        val edittextEditBack = view.findViewById<EditText>(R.id.edittextEditBack)
        val edittextEditContext = view.findViewById<EditText>(R.id.edittextEditContext)
        val textviewSaveCardChanges = view.findViewById<TextView>(R.id.textviewSaveCardChanges)
        val textviewCancel = view.findViewById<TextView>(R.id.textviewCancel)
        val btnGenerateAi = view.findViewById<LinearLayout>(R.id.btnGenerateAiBottomSheet)

        // Change title if we are Adding
        if (card == null) {
            view.findViewById<TextView>(R.id.textviewEditTitle).text = "Add New Card"
        } else {
            edittextEditFront.setText(card.frontText)
            edittextEditBack.setText(card.backText)
            edittextEditContext.setText(card.contextText ?: "")
        }

        // Handle the new AI button inside the Bottom Sheet
        btnGenerateAi.setOnClickListener {
            val front = edittextEditFront.text.toString().trim()
            val back = edittextEditBack.text.toString().trim()
            if (front.isEmpty() || back.isEmpty()) {
                toast("Please fill out the Front and Back first!")
                return@setOnClickListener
            }
            toast("Gemini is writing context...")
            presenter.generateContextForText(front, back) { generatedText ->
                edittextEditContext.setText(generatedText)
            }
        }

        textviewCancel.setOnClickListener { bottomSheetDialog.dismiss() }

        textviewSaveCardChanges.setOnClickListener {
            val newFront = edittextEditFront.text.toString().trim()
            val newBack = edittextEditBack.text.toString().trim()
            val newContext = edittextEditContext.text.toString().trim().takeIf { it.isNotEmpty() }

            if (newFront.isEmpty() || newBack.isEmpty()) {
                toast("Front and Back cannot be empty!")
                return@setOnClickListener
            }

            // Route to Add or Edit based on if card is null
            if (card == null) {
                presenter.onAddCardSaved(newFront, newBack, newContext)
            } else {
                presenter.onEditCardSaved(index, newFront, newBack, newContext)
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
}