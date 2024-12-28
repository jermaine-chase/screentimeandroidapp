package com.itgnomes.screentime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.itgnomes.screentime.auth.LoginActivity
import com.itgnomes.screentime.dashboard.ParentDashboardActivity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        try {
            val app = FirebaseApp.getInstance()
            Log.d("FirebaseInit", "Firebase initialized successfully: ${app.name}")
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Error initializing Firebase: ${e.message}")
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Navigate to Parent or Child Dashboard based on role
            val intent = Intent(this, ParentDashboardActivity::class.java) // Example
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}