package com.cargoexpress.app.core.domain

data class Alert(
    val id: Int,
    val title: String,
    val type: String,
    val description: String,
    val date: String,
    val tripId: Int
)
