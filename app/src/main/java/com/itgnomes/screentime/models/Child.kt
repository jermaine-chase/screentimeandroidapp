package com.itgnomes.screentime.models

data class Child(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val screenTime: Int = 0 // Remaining screen time in minutes
)
