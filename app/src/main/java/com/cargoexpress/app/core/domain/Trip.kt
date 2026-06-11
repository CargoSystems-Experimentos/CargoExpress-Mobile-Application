package com.cargoexpress.app.core.domain

data class Trip(
    val id: Int,
    val name: String,
    val state: String,
    val type: String,
    val weight: Double,
    val loadLocation: String,
    val loadDate: String,
    val unloadLocation: String,
    val unloadDate: String,
    val driverId: Int,
    val vehicleId: Int,
    val clientId: Int,
    val entrepreneurId: Int
)