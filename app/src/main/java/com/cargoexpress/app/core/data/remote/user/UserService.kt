package com.cargoexpress.app.core.data.remote.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int,
        @Header("Authorization") token: String
    ): Response<UserDto>

    @GET("users/{userId}/role")
    suspend fun getUserRole(
        @Path("userId") userId: Int,
        @Header("Authorization") token: String
    ): Response<UserRoleDto>

    @PUT("users/{userId}/state")
    suspend fun updateUserState(
        @Path("userId") userId: Int,
        @Header("Authorization") token: String,
        @Body update: UserStateUpdateDto
    ): Response<UserDto>
}
