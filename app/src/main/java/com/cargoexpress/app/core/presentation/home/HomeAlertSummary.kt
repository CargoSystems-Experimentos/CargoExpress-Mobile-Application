package com.cargoexpress.app.core.presentation.home

data class HomeAlertSummary(
    val tripName: String,
    val alertTitle: String,
    val alertType: String,
    val alertDate: String,
    val tripId: Int
)
