package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.login.LoginRequestDto
import com.cargoexpress.app.core.data.remote.login.LoginResponseDto
import com.cargoexpress.app.core.data.remote.login.LoginService
import com.cargoexpress.app.core.data.remote.login.GoogleSignInRequestDto
import com.cargoexpress.app.core.domain.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val loginService: LoginService) {

    fun signIn(username: String, password:String,callback: (Result<LoginResponse>) ->Unit){
        val requestDto = LoginRequestDto(username,password)

        loginService.signIn(requestDto).enqueue(object: Callback<LoginResponseDto>{
            override fun onResponse(call: Call<LoginResponseDto>, response: Response<LoginResponseDto>) {
                try {
                    if (response.isSuccessful){
                        response.body()?.let { responseDto ->
                            val loginResponse = LoginResponse(
                                id = responseDto.id,
                                username = responseDto.username,
                                token = responseDto.token
                            )
                            callback(Result.success(loginResponse))
                        } ?: callback(Result.failure(Exception("Response body is null")))
                    } else {
                        callback(Result.failure(Exception(ApiErrorParser.parse(response))))
                    }
                } catch (e: Exception){
                    callback(Result.failure(e))
                }
            }

            override fun onFailure(p0: Call<LoginResponseDto>, p1: Throwable) {
                callback(Result.failure(p1))
            }
        })
    }

    fun googleSignIn(idToken: String, callback: (Result<LoginResponse>) -> Unit) {
        val requestDto = GoogleSignInRequestDto(idToken)

        loginService.googleSignIn(requestDto).enqueue(object : Callback<LoginResponseDto> {
            override fun onResponse(call: Call<LoginResponseDto>, response: Response<LoginResponseDto>) {
                try {
                    if (response.isSuccessful) {
                        response.body()?.let { responseDto ->
                            val loginResponse = LoginResponse(
                                id = responseDto.id,
                                username = responseDto.username,
                                token = responseDto.token
                            )
                            callback(Result.success(loginResponse))
                        } ?: callback(Result.failure(Exception("Response body is null")))
                    } else {
                        callback(Result.failure(Exception(ApiErrorParser.parse(response))))
                    }
                } catch (e: Exception) {
                    callback(Result.failure(e))
                }
            }

            override fun onFailure(call: Call<LoginResponseDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
