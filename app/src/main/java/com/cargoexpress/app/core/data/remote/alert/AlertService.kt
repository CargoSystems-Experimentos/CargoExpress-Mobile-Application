package com.cargoexpress.app.core.data.remote.alert

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AlertService {
    @GET("alerts")
    suspend fun getAlerts(
        @Header("Authorization") token: String
    ): Response<List<AlertDto>>

    @GET("alerts/{alertId}")
    suspend fun getAlertById(
        @Path("alertId") alertId: Int,
        @Header("Authorization") token: String
    ): Response<AlertDto>

    @GET("trips/{tripId}/alerts")
    suspend fun getAlertsByTripId(
        @Path("tripId") tripId: Int,
        @Header("Authorization") token: String
    ): Response<List<AlertDto>>

    @POST("alerts")
    suspend fun createAlert(
        @Header("Authorization") token: String,
        @Body alert: AlertPostDto
    ): Response<AlertDto>
}
