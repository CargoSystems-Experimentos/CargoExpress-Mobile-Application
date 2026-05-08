package com.cargoexpress.app.core.domain

data class Driver(
    val id: Int,
    val name: String,
    val dni: String,
    val license: String,
    val contactNumber: String,
    val entrepreneurId: Int
)