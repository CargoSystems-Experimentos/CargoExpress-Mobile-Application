package com.cargoexpress.app.core.data.remote.vehicle

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VehicleService {
    @GET("vehicles/{vehicleId}")
    suspend fun getVehicleById(
        @Path("vehicleId") vehicleId: Int,
        @Header("Authorization") token: String
    ): Response<VehicleDto>

    @GET("entrepreneurs/{entrepreneurId}/vehicles")
    suspend fun getVehiclesByEntrepreneur(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<VehicleDto>>

    @POST("vehicles")
    suspend fun addVehicle(
        @Header("Authorization") token: String,
        @Body vehicle: VehiclePostDto
    ): Response<VehicleDto>

    @PUT("vehicles/{vehicleId}/name")
    suspend fun updateVehicleName(
        @Path("vehicleId") vehicleId: Int,
        @Header("Authorization") token: String,
        @Body update: VehicleNameUpdateDto
    ): Response<VehicleNameUpdateDto>

    @PUT("vehicles/{vehicleId}/state")
    suspend fun updateVehicleState(
        @Path("vehicleId") vehicleId: Int,
        @Header("Authorization") token: String,
        @Body update: VehicleStateUpdateDto
    ): Response<VehicleStateUpdateDto>
}
