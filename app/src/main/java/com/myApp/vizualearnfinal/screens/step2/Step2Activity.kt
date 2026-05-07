package com.myApp.vizualearnfinal.screens.step2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.data.database.AppDatabase
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.screens.step3.Step3Activity
import com.myApp.vizualearnfinal.utils.*
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.HorizontalScrollView

class  Step2Activity : AppCompatActivity(), Step2Contract.View {

    private lateinit var presenter: Step2Contract.Presenter
    private var setId: Int = 0
    private var creationType: String = ""
    private var inputMethod: String = ""

    // Store the selected file URIs as strings so they can be passed via Intent
    private val selectedImageUris = ArrayList<String>()
    private var selectedPdfUri: String? = null

    // multiple image
    private val pickMultipleImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            uris.forEach { selectedImageUris.add(it.toString()) } // Save them!
            toast("${uris.size} images selected!")
            displayImageThumbnails(uris)
        } else {
            toast("No images selected")
        }
    }

    // pdf
    private val pickPdfDocument = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedPdfUri = uri.toString() // Save it!
            val fileName = getFileName(uri)
            getTextView(R.id.textviewSelectedPdfName)?.text = "Selected: $fileName"
        } else {
            toast("No PDF selected")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step2_review)

        setId = intent.getIntExtra("EXTRA_SET_ID", 0)
        creationType = intent.getStringExtra("EXTRA_TYPE") ?: "flashcard"
        inputMethod = intent.getStringExtra("EXTRA_INPUT_METHOD") ?: "TEXT"

        val dao = AppDatabase.getDatabase(this).studySetDao()
        val repository = StudySetRepository(dao)
        presenter = Step2Presenter(this, Step2Model(repository))

        presenter.loadStudySetDetails(setId)

        setupDynamicUI()

        getLinearLayout(R.id.linearlayoutGenerateButton)?.setOnClickListener {
            val itemName = getEditTextStringValue(R.id.edittextName)
            presenter.validateAndGenerate(itemName)
        }

        getImageView(R.id.imageviewBack)?.setOnClickListener { finish() }

        // Wire up the new buttons we just made in XML!
        getLinearLayout(R.id.btnOpenGallery)?.setOnClickListener {
            pickMultipleImages.launch("image/*") // Only show images
        }

        getLinearLayout(R.id.btnOpenPdfPicker)?.setOnClickListener {
            pickPdfDocument.launch("application/pdf") // Only show PDFs
        }
    }

    // Helper: Draws the little square images on the screen
    private fun displayImageThumbnails(uris: List<Uri>) {
        val scrollView = findViewById<HorizontalScrollView>(R.id.scrollviewImageThumbnails)
        val thumbnailContainer = getLinearLayout(R.id.linearlayoutImageThumbnails)

        thumbnailContainer?.removeAllViews() // Clear old ones

        // Toggle visibility!
        if (uris.isNotEmpty()) {
            scrollView?.visibility = View.VISIBLE
        } else {
            scrollView?.visibility = View.GONE
        }

        for (uri in uris) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(250, 250).apply {
                    setMargins(0, 0, 16, 0) // Add spacing between images
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageURI(uri) // Load the image!
                clipToOutline = true
                setBackgroundResource(R.drawable.bg_rounded_card) // Give it rounded corners
            }
            thumbnailContainer?.addView(imageView)
        }
    }

    // Helper: Grabs the actual name of the PDF file from Android's file system
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = it.getString(index)
                }
            }
        }
        return result ?: uri.path?.substringAfterLast('/') ?: "Unknown Document"
    }

    private fun setupDynamicUI() {
        // Adjust text for Mind Map vs Flashcards
        if (creationType == "mindmap") {
            getTextView(R.id.textviewDynamicTitle)?.text = "Name your Mind Map"
            getTextView(R.id.textviewGenerateButtonText)?.text = "Generate Mind Map"
        } else {
            getTextView(R.id.textviewDynamicTitle)?.text = "Name your Flash Cards"
            getTextView(R.id.textviewGenerateButtonText)?.text = "Generate Flash Cards"
        }

        // Setup the specific variant (Text, Image, PDF)
        val textVariant = getEditText(R.id.edittextExtractedText)
        val imageVariant = getLinearLayout(R.id.linearlayoutUploadImage)
        val pdfVariant = getLinearLayout(R.id.linearlayoutUploadPdf)
        val variantTitle = getTextView(R.id.textviewVariantTitle)

        // Hide all first
        textVariant?.visibility = View.GONE
        imageVariant?.visibility = View.GONE
        pdfVariant?.visibility = View.GONE

        // Show the correct one
        when (inputMethod) {
            "IMAGE" -> {
                imageVariant?.visibility = View.VISIBLE
                variantTitle?.text = "Upload Image"
            }
            "PDF" -> {
                pdfVariant?.visibility = View.VISIBLE
                variantTitle?.text = "Upload PDF"
            }
            "TEXT" -> {
                textVariant?.visibility = View.VISIBLE
                variantTitle?.text = "Extracted Text – review before generating"
            }
        }
    }

    override fun displaySavingLocation(setName: String) {
        getTextView(R.id.textviewSavingInto)?.text = "Saving into: $setName"
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun navigateToStep3() {
        val intent = Intent(this, Step3Activity::class.java).apply {
            putExtra("EXTRA_SET_ID", setId)
            putExtra("EXTRA_TYPE", creationType)
            putExtra("EXTRA_ITEM_NAME", getEditTextStringValue(R.id.edittextName))
            putExtra("EXTRA_NOTES", getEditTextStringValue(R.id.edittextExtractedText))

            // NEW: Pass the file URIs!
            putStringArrayListExtra("EXTRA_IMAGE_URIS", selectedImageUris)
            putExtra("EXTRA_PDF_URI", selectedPdfUri)
            putExtra("EXTRA_INPUT_METHOD", inputMethod) // TEXT, IMAGE, or PDF
        }
        startActivity(intent)
    }
}