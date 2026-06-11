package com.cargoexpress.app.core.data.remote.user

import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.data.remote.trip.TripDto
import com.cargoexpress.app.core.data.remote.vehicle.VehicleDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface EntrepreneurService {
    @GET("entrepreneurs/{entrepreneurId}")
    suspend fun getEntrepreneurById(
        @Path("entrepreneurId") id: Int,
        @Header("Authorization") token: String
    ): Response<EntrepreneurDto>

    @GET("entrepreneurs/{entrepreneurId}/trips")
    suspend fun getTripsByEntrepreneurId(
        @Path("entrepreneurId") id: Int,
        @Header("Authorization") token: String
    ): Response<List<TripDto>>

    @GET("entrepreneurs/{entrepreneurId}/clients")
    suspend fun getClientsByEntrepreneurId(
        @Path("entrepreneurId") id: Int,
        @Header("Authorization") token: String
    ): Response<List<ClientDto>>

    @GET("entrepreneurs/{entrepreneurId}/vehicles")
    suspend fun getVehiclesByEntrepreneurId(
        @Path("entrepreneurId") id: Int,
        @Header("Authorization") token: String
    ): Response<List<VehicleDto>>

    @GET("entrepreneurs/{entrepreneurId}/drivers")
    suspend fun getDriversByEntrepreneurId(
        @Path("entrepreneurId") id: Int,
        @Header("Authorization") token: String
    ): Response<List<DriverDto>>
}
