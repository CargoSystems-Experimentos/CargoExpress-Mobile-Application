package com.cargoexpress.app.core.data.remote.user

import com.cargoexpress.app.core.data.remote.trip.TripDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ClientService {
    @GET("clients/{id}")
    suspend fun getClient(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<ClientDto>

    @GET("clients/dni/{dni}")
    suspend fun getClientByDni(
        @Path("dni") dni: String,
        @Header("Authorization") token: String
    ): Response<ClientDto>

    @GET("clients/{clientId}/trips")
    suspend fun getTripsByClientId(
        @Path("clientId") clientId: Int,
        @Header("Authorization") token: String
    ): Response<List<TripDto>>
}
