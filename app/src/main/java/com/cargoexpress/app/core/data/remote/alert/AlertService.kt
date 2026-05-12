package com.cargoexpress.app.core.data.remote.alert

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AlertService {
    @GET("alerts")
    suspend fun getAlerts(
        @Header("Authorization") token: String
    ): Response<List<AlertDto>>

    @POST("alerts")
    suspend fun createAlert(
        @Header("Authorization") token: String,
        @Body alert: AlertDto
    ): Response<AlertDto>
}