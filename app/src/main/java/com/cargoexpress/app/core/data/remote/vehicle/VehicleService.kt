package com.cargoexpress.app.core.data.remote.vehicle

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface VehicleService {
    @GET("entrepreneurs/{entrepreneurId}/vehicles")
    suspend fun getVehicles(
        @Header("Authorization") token: String,
        @Path("entrepreneurId") entrepreneurId: Int
    ): Response<List<VehicleDto>>

    @GET("entrepreneur/{entrepreneurId}/vehicles")
    suspend fun getVehiclesDirectByEntrepreneur(
        @Header("Authorization") token: String,
        @Path("entrepreneurId") entrepreneurId: Int
    ): Response<List<VehicleDto>>

    @POST("vehicles")
    suspend fun addVehicle(
        @Header("Authorization") token: String,
        @Body vehicle: VehicleDto
    ): Response<VehicleDto>
}