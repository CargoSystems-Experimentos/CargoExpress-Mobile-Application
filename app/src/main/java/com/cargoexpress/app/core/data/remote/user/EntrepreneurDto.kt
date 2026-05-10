package com.cargoexpress.app.core.data.remote.user

data class EntrepreneurDto(
    val id: Int,
    val name: String,
    val phone: String,
    val ruc: String,
    val logoImage: String,
    val userId: Int
)