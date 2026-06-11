package com.cargoexpress.app.core.data.remote.register

data class ClientProfileDto(
    val name: String,
    val dni: String,
    val birthDate: String
)

data class EntrepreneurProfileDto(
    val name: String,
    val ruc: String,
    val address: String
)

data class RegisterClientRequestDto(
    val username: String,
    val password: String,
    val phone: String,
    val role: Boolean = false,
    val profile: ClientProfileDto
)

data class RegisterEntrepreneurRequestDto(
    val username: String,
    val password: String,
    val phone: String,
    val role: Boolean = true,
    val profile: EntrepreneurProfileDto
)
