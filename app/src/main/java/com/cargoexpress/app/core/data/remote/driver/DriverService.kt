package com.cargoexpress.app.core.data.remote.driver

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DriverService {
    @GET("drivers/{driverId}")
    suspend fun getDriverById(
        @Path("driverId") driverId: Int,
        @Header("Authorization") token: String
    ): Response<DriverDto>

    @GET("entrepreneurs/{entrepreneurId}/drivers")
    suspend fun getDriversByEntrepreneur(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<DriverDto>>

    @POST("drivers")
    suspend fun addDriver(
        @Header("Authorization") token: String,
        @Body driver: DriverPostDto
    ): Response<DriverDto>

    @PUT("drivers/{driverId}")
    suspend fun updateDriver(
        @Path("driverId") driverId: Int,
        @Header("Authorization") token: String,
        @Body update: DriverUpdateDto
    ): Response<DriverUpdateDto>

    @PUT("drivers/{driverId}/state")
    suspend fun updateDriverState(
        @Path("driverId") driverId: Int,
        @Header("Authorization") token: String,
        @Body update: DriverStateUpdateDto
    ): Response<DriverStateUpdateDto>
}
