package com.cargoexpress.app.core.data.remote.user

data class ClientDto(
    val id: Int,
    val name: String,
    val phone: String,
    val dni: String,
    val userId: Int
)