package com.cargoexpress.app.core.data.remote.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ClientService {


    @POST("clients")
    suspend fun createClient(@Body request: ClientRequestDto, @Header("Authorization") token: String): Response<ClientDto>

    @GET("clients/{id}")
    suspend fun getClient(@Path("id") id: Int, @Header("Authorization") token: String): Response<ClientDto>

    @GET("clients")
    suspend fun getClients(@Header("Authorization") token: String): Response<List<ClientDto>>
}