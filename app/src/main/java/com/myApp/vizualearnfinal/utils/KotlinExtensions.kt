package com.myApp.vizualearnfinal.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.myApp.vizualearnfinal.R
import com.myApp.vizualearnfinal.screens.addset.AddSetActivity
import com.myApp.vizualearnfinal.screens.dashboard.DashboardActivity
import com.myApp.vizualearnfinal.screens.mystudysets.MyStudySetsActivity
import com.myApp.vizualearnfinal.screens.profile.ProfileActivity
import com.myApp.vizualearnfinal.screens.progress.ProgressActivity

// --- ACTIVITY EXTENSIONS ---

fun Activity.setupUniversalFooter() {

    // 1. Home / Dashboard
    getImageView(R.id.imageviewHomeButton)?.setOnClickListener {
        if (this !is DashboardActivity) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    // 2. View Sets (My Study Sets)
    getImageView(R.id.imageviewViewSetsButton)?.setOnClickListener {
        if (this !is MyStudySetsActivity) {
            val intent = Intent(this, MyStudySetsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    // 3. Center Add Button (+)
    getImageView(R.id.imageviewAddSetButton)?.setOnClickListener {
        if (this !is AddSetActivity) {
            val intent = Intent(this, AddSetActivity::class.java)
            // No clear top flag here, because adding a set is a temporary task!
            startActivity(intent)
        }
    }

    // 4. Progress / Streak
    getImageView(R.id.imageviewViewStreakButton)?.setOnClickListener {
        if (this !is ProgressActivity) {
            val intent = Intent(this, ProgressActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    // 5. Profile
    getImageView(R.id.imageviewViewProfileButton)?.setOnClickListener {
        if (this !is ProfileActivity) {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
}
fun Activity.getEditTextStringValue(id: Int): String {
    return findViewById<EditText>(id).text.toString()
}

fun Activity.toast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.start(toClass: Class<*>?) {
    startActivity(Intent(this, toClass))
}

fun Activity.getButtonView(id: Int): Button? {
    return findViewById<Button>(id)
}

fun Activity.getImageView(id: Int): ImageView? {
    return findViewById<ImageView>(id)
}

fun Activity.getTextView(id: Int): TextView? {
    return findViewById<TextView>(id)
}

fun Activity.setTextViewStringValue(id: Int, textValue: String) {
    findViewById<TextView>(id)?.text = textValue
}

fun Activity.getLinearLayout(id: Int): LinearLayout? {
    return findViewById<LinearLayout>(id)
}

fun Activity.getEditText(id: Int): EditText? {
    return findViewById<EditText>(id)
}

fun Activity.getFrameLayout(id: Int): FrameLayout? = findViewById(id)

fun Activity.getRelativeLayout(id: Int): RelativeLayout? {
    return findViewById<RelativeLayout>(id)
}
// --- VIEW EXTENSIONS (New: For inflated layouts like RecyclerView items) ---

fun View.getImageView(id: Int): ImageView? {
    return findViewById<ImageView>(id)
}

fun View.getTextView(id: Int): TextView? {
    return findViewById<TextView>(id)
}