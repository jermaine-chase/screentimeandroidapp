package com.itgnomes.screentime.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.itgnomes.screentime.R
import com.itgnomes.screentime.dashboard.ChildDashboardActivity
import com.itgnomes.screentime.dashboard.ParentDashboardActivity

class RegisterActivity : Activity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val registerButton: Button = findViewById(R.id.registerButton)
        val emailField: EditText = findViewById(R.id.emailField)
        val passwordField: EditText = findViewById(R.id.passwordField)
        val roleSpinner: Spinner = findViewById(R.id.roleSpinner)
        val loginText: TextView = findViewById(R.id.loginText)

        registerButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val role = roleSpinner.selectedItem.toString()

            if (role == "Select a Role") {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserToDatabase(email, role)
                        } else {
                            Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Closes the current activity
        }
    }

    private fun saveUserToDatabase(email: String, role: String) {
        val userId = auth.currentUser?.uid
        val userRef = database.getReference("users").child(userId!!)

        val userMap = mapOf(
            "email" to email,
            "role" to role
        )

        userRef.setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                    navigateToDashboard(role)
                } else {
                    Toast.makeText(this, "Database Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "Parent" -> Intent(this, ParentDashboardActivity::class.java)
            "Child" -> Intent(this, ChildDashboardActivity::class.java)
            else -> null
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
