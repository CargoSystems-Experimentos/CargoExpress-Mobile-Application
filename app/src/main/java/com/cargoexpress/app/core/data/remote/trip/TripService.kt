package com.cargoexpress.app.core.data.remote.trip


import com.cargoexpress.app.core.data.remote.expense.ExpenseDto
import com.cargoexpress.app.core.domain.Expense
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface TripService {

    @GET("entrepreneurs/{entrepreneurId}/trips")
    suspend fun getTrips(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<TripDto>>

    @GET("trips")
    suspend fun getTrips(@Header("Authorization") token: String): Response<List<TripDto>>

    @GET("trips/{id}")
    suspend fun getTrip(@Path("id") id: Int, @Header("Authorization") token: String): Response<TripDto>


    @POST("trips")
    suspend fun addTrip(
        @Header("Authorization") token: String,
        @Body trip: TripDtoPost
    ): Response<TripDtoPost>

    @PUT("trips/{id}")
    suspend fun updateTrip(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body trip: TripDtoPost
    ): Response<TripDtoPost>

    @GET("trips/{tripId}/expense")
    suspend fun getExpenseByTripId(
        @Path("tripId") tripId: Int,
        @Header("Authorization") token: String
    ): Response<Expense>

}