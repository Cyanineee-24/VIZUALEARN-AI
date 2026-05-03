package com.myApp.vizualearnfinal.screens.mindmapview

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import java.io.OutputStream

class MindMapViewActivity : AppCompatActivity(), MindMapViewContract.View {

    private lateinit var presenter: MindMapViewContract.Presenter
    private lateinit var webView: WebView

    private var isPageLoaded = false
    private var pendingJsonData: String? = null

    // --- THE JAVASCRIPT BRIDGE ---
    inner class WebAppInterface {
        @JavascriptInterface
        fun onNodeTapped(title: String, description: String) {
            runOnUiThread {
                presenter.onNodeTapped(title, description)
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

                    // THE CRITICAL SUCCESS FEEDBACK
                    runOnUiThread {
                        toast("Success! Mind Map saved to Gallery/VizuaLearn")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { toast("Export failed: ${e.localizedMessage}") }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_map_view)

        val mapId = intent.getIntExtra("EXTRA_MAP_ID", -1)
        val mapTitle = intent.getStringExtra("EXTRA_MAP_TITLE") ?: "Mind Map"

        val repository = StudySetRepository(AppDatabase.getDatabase(this).studySetDao())
        presenter = MindMapViewPresenter(this, MindMapViewModel(repository))

        findViewById<ImageView>(R.id.imageviewBack).setOnClickListener { finish() }

        webView = findViewById(R.id.webViewCytoscape)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(WebAppInterface(), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                isPageLoaded = true
                pendingJsonData?.let { base64Json ->
                    webView.evaluateJavascript("javascript:loadGraphBase64('$base64Json')", null)
                    pendingJsonData = null
                }
            }
        }

        webView.loadUrl("file:///android_asset/mindmap.html")
        presenter.loadMapData(mapId, mapTitle)

        getLinearLayout(R.id.linearlayoutBtnExport)?.setOnClickListener {
            val expandAndExportJS = """
                javascript:(function() {
                    try {
                        if (typeof cy !== 'undefined') {
                            // 1. Force reveal all nodes using your custom HTML logic
                            cy.elements().removeClass('hidden');
                            cy.nodes().removeClass('has-hidden-children');
                            
                            // 2. Snap to a clean layout
                            cy.layout({
                                name: 'dagre',
                                rankDir: 'LR',
                                animate: false,
                                fit: true,
                                padding: 30
                            }).run();
                        }
                    } catch(e) {}
                    // 3. Take the picture after a short delay to ensure rendering is 100%
                    setTimeout(function() { exportToAndroid(); }, 1000);
                })()
            """.trimIndent()
            webView.evaluateJavascript(expandAndExportJS, null)
        }
    }

    override fun setMapHeaders(title: String, subtitle: String) {
        findViewById<TextView>(R.id.textviewMapTitle).text = title
        findViewById<TextView>(R.id.textviewMapSubtitle).text = subtitle
    }

    override fun injectGraphData(jsonString: String) {
        if (isPageLoaded) {
            webView.evaluateJavascript("javascript:loadGraphBase64('$jsonString')", null)
        } else {
            pendingJsonData = jsonString
        }
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