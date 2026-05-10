package com.abhiumale.kartikmartapp

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KartikMartApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
