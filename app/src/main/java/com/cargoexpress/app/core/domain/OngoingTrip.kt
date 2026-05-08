package com.cargoexpress.app.core.domain

data class OngoingTrip(
    val latitude: Float,
    val longitude: Float,
    val speed: Int,
    val distance: Int,
    val tripId: Int
)
