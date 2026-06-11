package com.cargoexpress.app.core.data.remote.user

import com.cargoexpress.app.core.domain.Entrepreneur

data class EntrepreneurDto(
    val id: Int,
    val name: String,
    val ruc: String,
    val address: String,
    val userId: Int
)

fun EntrepreneurDto.toEntrepreneur() = Entrepreneur(
    id = id,
    name = name,
    ruc = ruc,
    address = address,
    userId = userId
)
