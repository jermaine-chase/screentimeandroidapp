package com.itgnomes.screentime.child

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itgnomes.screentime.R
import com.itgnomes.screentime.models.Child

class ChildAdapter(
    private val children: List<Child>
) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childName: TextView = itemView.findViewById(R.id.childName)
        val childEmail: TextView = itemView.findViewById(R.id.childEmail)
        val screenTime: TextView = itemView.findViewById(R.id.screenTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val child = children[position]
        holder.childName.text = child.name
        holder.childEmail.text = child.email
        holder.screenTime.text = "Remaining Time: ${child.screenTime} mins"
    }

    override fun getItemCount(): Int = children.size
}
