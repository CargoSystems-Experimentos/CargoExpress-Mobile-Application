package com.cargoexpress.app.core.data.remote.user

import com.cargoexpress.app.core.domain.Client

data class ClientDto(
    val id: Int,
    val name: String,
    val dni: String,
    val birthDate: String,
    val userId: Int
)

fun ClientDto.toClient() = Client(
    id = id,
    name = name,
    dni = dni,
    birthDate = birthDate,
    userId = userId
)
