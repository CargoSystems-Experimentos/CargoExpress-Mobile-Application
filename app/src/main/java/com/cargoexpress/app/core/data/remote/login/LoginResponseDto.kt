package com.cargoexpress.app.core.data.remote.login

import com.cargoexpress.app.core.domain.LoginResponse

data class LoginResponseDto(
    val id: Int,
    val username: String,
    val token: String
)

