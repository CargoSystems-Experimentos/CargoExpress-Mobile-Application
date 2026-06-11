package com.cargoexpress.app.core.data.remote.trip

import com.cargoexpress.app.core.data.remote.alert.AlertDto
import com.cargoexpress.app.core.data.remote.expense.ExpenseDto
import com.cargoexpress.app.core.data.remote.ongoingtrip.OngoingTripDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TripService {

    @GET("entrepreneurs/{entrepreneurId}/trips")
    suspend fun getTripsByEntrepreneur(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<TripDto>>

    @GET("clients/{clientId}/trips")
    suspend fun getTripsByClient(
        @Path("clientId") clientId: Int,
        @Header("Authorization") token: String
    ): Response<List<TripDto>>

    @GET("trips/{id}")
    suspend fun getTrip(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<TripDto>

    @POST("trips")
    suspend fun addTrip(
        @Header("Authorization") token: String,
        @Body trip: TripPostDto
    ): Response<TripDto>

    @PUT("trips/{id}/details")
    suspend fun updateTripDetails(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body trip: TripDetailsUpdateDto
    ): Response<TripDetailsUpdateDto>

    @PUT("trips/{id}/schedule")
    suspend fun updateTripSchedule(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body schedule: TripScheduleUpdateDto
    ): Response<TripScheduleUpdateDto>

    @PUT("trips/{id}/state")
    suspend fun updateTripState(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body state: TripStateUpdateDto
    ): Response<TripStateUpdateDto>

    @GET("trips/{tripId}/alerts")
    suspend fun getAlertsByTripId(
        @Path("tripId") tripId: Int,
        @Header("Authorization") token: String
    ): Response<List<AlertDto>>

    @GET("trips/{tripId}/ongoing-trips")
    suspend fun getOngoingTripByTripId(
        @Path("tripId") tripId: Int,
        @Header("Authorization") token: String
    ): Response<OngoingTripDto>

    @GET("trips/{tripId}/expenses")
    suspend fun getExpenseByTripId(
        @Path("tripId") tripId: Int,
        @Header("Authorization") token: String
    ): Response<ExpenseDto>
}
