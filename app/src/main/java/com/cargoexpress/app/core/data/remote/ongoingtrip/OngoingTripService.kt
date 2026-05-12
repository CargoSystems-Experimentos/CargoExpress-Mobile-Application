package com.cargoexpress.app.core.data.remote.ongoingtrip

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OngoingTripService {
    @GET("ongoing-trips")
    suspend fun getOngoingTrips(
        @Header("Authorization") token: String
    ): Response<List<OngoingTripDto>>

    @POST("ongoing-trips")
    suspend fun createOngoingTrip(
        @Header("Authorization") token: String,
        @Body ongoingTrip: OngoingTripDtoPost
    ): Response<OngoingTripDto>
}