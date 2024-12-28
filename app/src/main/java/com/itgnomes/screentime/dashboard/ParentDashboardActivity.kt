package com.itgnomes.screentime.dashboard

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itgnomes.screentime.R
import com.itgnomes.screentime.child.AddChildDialog
import com.itgnomes.screentime.child.ChildAdapter
import com.itgnomes.screentime.models.Child

class ParentDashboardActivity : Activity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var childAdapter: ChildAdapter
    private lateinit var childrenList: MutableList<Child>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_dashboard)

        recyclerView = findViewById(R.id.childrenRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addChildButton: Button = findViewById(R.id.addChildButton)

        childrenList = mutableListOf()
        childAdapter = ChildAdapter(childrenList)
        recyclerView.adapter = childAdapter

        database = FirebaseDatabase.getInstance().reference
        fetchChildren()

        addChildButton.setOnClickListener {
            AddChildDialog(this) { name, email ->
                addChildToFirebase(name, email)
            }.show()
        }
    }

    private fun fetchChildren() {
        val parentId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val parentRef = database.child("users").child(parentId).child("children")

        parentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                childrenList.clear()
                for (childSnapshot in snapshot.children) {
                    val childId = childSnapshot.key ?: continue
                    fetchChildDetails(childId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ParentDashboard", "Error fetching children: ${error.message}")
                Toast.makeText(this@ParentDashboardActivity, "Failed to fetch children", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchChildDetails(childId: String) {
        database.child("users").child(childId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val child = snapshot.getValue(Child::class.java)
                if (child != null) {
                    childrenList.add(child)
                    childAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ParentDashboard", "Error fetching child details: ${error.message}")
            }
        })
    }

    private fun addChildToFirebase(name: String, email: String) {
        val parentId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val childId = database.child("users").push().key ?: return

        val childData = mapOf(
            "name" to name,
            "email" to email,
            "role" to "Child",
            "screenTime" to 0 // Initialize screen time to 0 minutes
        )

        val updates = mapOf(
            "/users/$childId" to childData,
            "/users/$parentId/children/$childId" to true
        )

        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Child added successfully", Toast.LENGTH_SHORT).show()
                fetchChildren() // Refresh the list
            } else {
                Toast.makeText(this, "Failed to add child: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
