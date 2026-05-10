package com.cargoexpress.app.core.data.remote.user

data class EntrepreneurRequestDto (
    val name: String,
    val phone: String,
    val ruc: String,
    val logoImage: String,
    val userId: Int
)