package com.cargoexpress.app.core.data.remote.alert
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header


interface AlertService {
    @GET("alerts")
    suspend fun getAlerts(
        @Header("Authorization") token: String
    ): Response<List<AlertDto>>
}