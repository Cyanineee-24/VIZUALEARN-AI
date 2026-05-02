package com.myApp.vizualearnfinal.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.myApp.vizualearnfinal.data.User

class CustomApplication: Application() {

    var registeredUser: User? = null

    var loginUser: User? = null

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}