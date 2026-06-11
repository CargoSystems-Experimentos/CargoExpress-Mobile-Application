package com.cargoexpress.app.core.domain

data class Vehicle(
    val id: Int,
    val name: String,
    val model: String,
    val plate: String,
    val tractorPlate: String,
    val maxLoad: Double,
    val volume: Double,
    val state: String,
    val entrepreneurId: Int
)