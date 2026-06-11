package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.register.ClientProfileDto
import com.cargoexpress.app.core.data.remote.register.EntrepreneurProfileDto
import com.cargoexpress.app.core.data.remote.register.RegisterClientRequestDto
import com.cargoexpress.app.core.data.remote.register.RegisterEntrepreneurRequestDto
import com.cargoexpress.app.core.data.remote.register.RegisterResponseDto
import com.cargoexpress.app.core.data.remote.register.RegisterService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository(private val registerService: RegisterService) {

    fun registerClient(
        username: String,
        password: String,
        phone: String,
        name: String,
        dni: String,
        birthDate: String,
        callback: (Result<String>) -> Unit
    ) {
        val requestDto = RegisterClientRequestDto(
            username = username,
            password = password,
            phone = phone,
            profile = ClientProfileDto(name = name, dni = dni, birthDate = birthDate)
        )
        registerService.signUpClient(requestDto).enqueue(object : Callback<RegisterResponseDto> {
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

    fun registerEntrepreneur(
        username: String,
        password: String,
        phone: String,
        name: String,
        ruc: String,
        address: String,
        callback: (Result<String>) -> Unit
    ) {
        val requestDto = RegisterEntrepreneurRequestDto(
            username = username,
            password = password,
            phone = phone,
            profile = EntrepreneurProfileDto(name = name, ruc = ruc, address = address)
        )
        registerService.signUpEntrepreneur(requestDto).enqueue(object : Callback<RegisterResponseDto> {
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
