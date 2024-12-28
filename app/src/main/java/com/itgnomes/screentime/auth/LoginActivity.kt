package com.itgnomes.screentime.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itgnomes.screentime.R
import com.itgnomes.screentime.dashboard.ChildDashboardActivity
import com.itgnomes.screentime.dashboard.ParentDashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val emailField: EditText = findViewById(R.id.emailField)
        val passwordField: EditText = findViewById(R.id.passwordField)
        val loginButton: Button = findViewById(R.id.loginButton)
        val registerText: TextView = findViewById(R.id.registerText)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            checkUserRole()
                        } else {
                            Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserRole() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val role = snapshot.child("role").getValue(String::class.java)
                if (role != null) {
                    navigateToDashboard(role)
                } else {
                    Toast.makeText(this@LoginActivity, "Role not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LoginActivity", "Error checking user role: ${error.message}")
                Toast.makeText(this@LoginActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "Parent" -> Intent(this, ParentDashboardActivity::class.java)
            "Child" -> Intent(this, ChildDashboardActivity::class.java)
            else -> null
        }
        if (intent != null) {
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Unknown role: $role", Toast.LENGTH_SHORT).show()
        }
    }
}
