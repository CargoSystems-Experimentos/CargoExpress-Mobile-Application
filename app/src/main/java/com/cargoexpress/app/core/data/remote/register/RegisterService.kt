package com.cargoexpress.app.core.data.remote.register

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {
    @POST("authentication/sign-up")
    fun signUpClient(@Body request: RegisterClientRequestDto): Call<RegisterResponseDto>

    @POST("authentication/sign-up")
    fun signUpEntrepreneur(@Body request: RegisterEntrepreneurRequestDto): Call<RegisterResponseDto>
}
