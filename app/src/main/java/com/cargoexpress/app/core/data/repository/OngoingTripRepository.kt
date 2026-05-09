package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.ongoingtrip.OngoingTripService
import com.cargoexpress.app.core.data.remote.ongoingtrip.toOngoingTrip
import com.cargoexpress.app.core.domain.OngoingTrip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cargoexpress.app.core.common.Resource

class OngoingTripRepository(private val ongoingTripService: OngoingTripService) {

    suspend fun getOngoingTrips(token: String): Resource<List<OngoingTrip>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val response = ongoingTripService.getOngoingTrips("Bearer $token")
            if (response.isSuccessful) {
                val ongoingTrips = response.body()?.map { it.toOngoingTrip() } ?: emptyList()
                Resource.Success(data = ongoingTrips)
            } else {
                Resource.Error(message = "Failed to fetch ongoing trips")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}