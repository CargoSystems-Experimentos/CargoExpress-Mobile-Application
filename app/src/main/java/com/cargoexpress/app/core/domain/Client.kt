package com.cargoexpress.app.core.domain

data class Client(
    val id: Int,
    val name: String,
    val dni: String,
    val birthDate: String,
    val userId: Int
)
