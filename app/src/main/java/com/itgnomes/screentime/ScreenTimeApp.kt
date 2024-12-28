package com.itgnomes.screentime

import android.app.Application
import com.google.firebase.FirebaseApp

class ScreenTimeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
