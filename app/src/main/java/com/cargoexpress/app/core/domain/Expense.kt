package com.cargoexpress.app.core.domain

data class Expense(
    val id: Int,
    val fuelAmount: Double,
    val fuelDescription: String,
    val viaticsAmount: Double,
    val viaticsDescription: String,
    val tollsAmount: Double,
    val tollsDescription: String,
    val tripId: Int
)