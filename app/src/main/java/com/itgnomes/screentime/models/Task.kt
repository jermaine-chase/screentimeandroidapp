package com.itgnomes.screentime.models

data class Task(
    val id: String,
    val title: String,
    val type: String, // e.g., "chore" or "app usage"
    val duration: Int?, // For app usage tasks
    val status: String, // e.g., "pending", "completed", "approved"
    val images: List<String>? // Before-and-after photos
)

