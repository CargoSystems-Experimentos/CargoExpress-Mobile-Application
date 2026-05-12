package com.cargoexpress.app.core.domain

data class Expense(
    val id: Int,
    val fuelAmount: Int,
    val fuelDescription: String,
    val viaticsAmount: Int,
    val viaticsDescription: String,
    val tollsAmount: Int,
    val tollsDescription: String,
    val tripId: Int
)