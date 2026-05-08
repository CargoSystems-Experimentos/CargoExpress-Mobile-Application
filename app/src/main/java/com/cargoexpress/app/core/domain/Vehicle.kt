package com.cargoexpress.app.core.domain

data class Vehicle (
    val id: Int,
    val model: String,
    val plate: String,
    val tractorPlate: String,
    val maxLoad: Float,
    val volume: Float,
    val entrepreneurId: Int
)