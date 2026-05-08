package com.cargoexpress.app.core.data.remote.login

import com.cargoexpress.app.core.domain.LoginRequest

data class LoginRequestDto(
    val username: String,
    val password: String
)

