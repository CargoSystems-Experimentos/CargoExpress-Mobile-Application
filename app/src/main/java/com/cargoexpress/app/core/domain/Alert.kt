package com.cargoexpress.app.core.domain

import java.time.LocalDateTime

data class Alert(
    val title: String,
    val description: String,
    val date: String,
    val tripId: Int
)