package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.register.RegisterRequestDto
import com.cargoexpress.app.core.data.remote.register.RegisterResponseDto
import com.cargoexpress.app.core.data.remote.register.RegisterService
import com.cargoexpress.app.core.domain.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository(private val registerService: RegisterService) {

    fun registerUser(username: String, password: String, callback: (Result<String>) -> Unit) {
        val requestDto = RegisterRequestDto(username, password)

        registerService.signUp(requestDto).enqueue(object : Callback<RegisterResponseDto> {
            override fun onResponse(call: Call<RegisterResponseDto>, response: Response<RegisterResponseDto>) {
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(Result.success(it.message))
                        } ?: callback(Result.failure(Exception("Response body is null")))
                    } else {
                        callback(Result.failure(Exception("Error: ${response.code()}")))
                    }
                } catch (e: Exception) {
                    callback(Result.failure(e))
                }
            }

            override fun onFailure(call: Call<RegisterResponseDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}