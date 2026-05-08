package com.cargoexpress.app.core.data.remote.login

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("authentication/sign-in")
    fun signIn(@Body request: LoginRequestDto): Call<LoginResponseDto>
}