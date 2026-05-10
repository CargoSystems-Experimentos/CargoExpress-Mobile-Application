package com.cargoexpress.app.core.domain

data class Trip(
    val id: Int,
    val name: String,
    val type: String,
    val weight: Int,
    val loadLocation: String,
    val loadDate: String,
    val unloadLocation: String,
    val unloadDate: String,
    val driverId: Int,
    val vehicleId: Int,
    val clientId: Int,
    val entrepreneurId: Int,
    val evidenceImg: String
)