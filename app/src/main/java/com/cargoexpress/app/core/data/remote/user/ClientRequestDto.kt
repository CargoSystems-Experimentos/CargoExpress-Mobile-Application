package com.cargoexpress.app.core.data.remote.user

data class ClientRequestDto(
    val name: String,
    val phone: String,
    val ruc: String,
    val address: String,
    val userId: Int
)
