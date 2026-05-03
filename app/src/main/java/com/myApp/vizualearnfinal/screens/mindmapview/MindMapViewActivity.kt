package com.myApp.vizualearnfinal.screens.mindmapview

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.getLinearLayout
import com.myApp.vizualearnfinal.utils.toast

class MindMapViewActivity : AppCompatActivity(), MindMapViewContract.View {

    private lateinit var presenter: MindMapViewContract.Presenter
    private lateinit var webView: WebView

    // --- THE JAVASCRIPT BRIDGE ---
    inner class WebAppInterface {
        @JavascriptInterface
        fun onNodeTapped(title: String, description: String) {
            // Pass the tap event to the Presenter on the main thread
            runOnUiThread {
                presenter.onNodeTapped(title, description)
            }
        }

        @JavascriptInterface
        fun saveImage(base64String: String) {
            // Remove the data URL prefix to get the pure Base64 string
            val cleanBase64 = base64String.replace("data:image/png;base64,", "")
            val imageBytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)
            // You can now write these imageBytes to the MediaStore/Gallery!
            runOnUiThread { toast("Map exported successfully!") }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_map_view)

        // 1. Get Intent Data
        val mapId = intent.getIntExtra("EXTRA_MAP_ID", -1)
        val mapTitle = intent.getStringExtra("EXTRA_MAP_TITLE") ?: "Main Concept"

        // 2. Setup MVP
        val repository = StudySetRepository(AppDatabase.getDatabase(this).studySetDao())
        presenter = MindMapViewPresenter(this, MindMapViewModel(repository))

        // 3. Setup Basic UI
        findViewById<ImageView>(R.id.imageviewBack).setOnClickListener { finish() }

        // 4. Setup WebView
        webView = findViewById(R.id.webViewCytoscape)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Register the bridge so HTML can talk to Kotlin
        webView.addJavascriptInterface(WebAppInterface(), "Android")

        // Load the local HTML file
        webView.loadUrl("file:///android_asset/mindmap.html")

        // 5. Tell the Presenter to load the data!
        // Slight delay ensures the HTML canvas is ready to receive data.
        webView.postDelayed({
            presenter.loadMapData(mapId, mapTitle)
        }, 500)

        getLinearLayout(R.id.linearlayoutBtnExport)?.setOnClickListener {
            webView.evaluateJavascript("javascript:exportToAndroid()", null)
        }
    }

    // --- VIEW CONTRACT IMPLEMENTATIONS ---

    override fun setMapHeaders(title: String, subtitle: String) {
        findViewById<TextView>(R.id.textviewMapTitle).text = title
        findViewById<TextView>(R.id.textviewMapSubtitle).text = subtitle
    }

    override fun injectGraphData(jsonString: String) {
        // Execute the JS function inside the WebView
        webView.evaluateJavascript("javascript:loadGraph('$jsonString')", null)
    }

    override fun updateNodeDetailsCard(title: String, description: String) {
        findViewById<TextView>(R.id.textviewNodeTitle).text = title
        findViewById<TextView>(R.id.textviewNodeDesc).text = description
    }

    override fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }
}