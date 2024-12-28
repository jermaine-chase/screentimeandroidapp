package com.itgnomes.screentime.dashboard

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itgnomes.screentime.R
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

        childrenList = mutableListOf()
        childAdapter = ChildAdapter(childrenList)
        recyclerView.adapter = childAdapter

        database = FirebaseDatabase.getInstance().reference
        fetchChildren()
    }

    private fun fetchChildren() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = database.child("users").child(userId).child("children")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                childrenList.clear()
                for (childSnapshot in snapshot.children) {
                    val childId = childSnapshot.key ?: continue
                    database.child("users").child(childId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(childDetailSnapshot: DataSnapshot) {
                            val child = childDetailSnapshot.getValue(Child::class.java)
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
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ParentDashboard", "Error fetching children: ${error.message}")
            }
        })
    }
}
