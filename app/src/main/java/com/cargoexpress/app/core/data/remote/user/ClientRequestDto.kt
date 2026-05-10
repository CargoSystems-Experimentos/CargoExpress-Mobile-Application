package com.cargoexpress.app.core.data.remote.user

data class ClientRequestDto(
    val name: String,
    val phone: String,
    val dni: String,
    val userId: Int
)