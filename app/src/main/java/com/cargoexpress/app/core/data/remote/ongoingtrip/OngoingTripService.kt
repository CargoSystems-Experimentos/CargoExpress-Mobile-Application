package com.cargoexpress.app.core.data.remote.ongoingtrip

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface OngoingTripService {
    @GET("ongoing-trips")
    suspend fun getOngoingTrips(
        @Header("Authorization") token: String
    ): Response<List<OngoingTripDto>>
}