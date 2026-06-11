package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.ongoingtrip.OngoingTripService
import com.cargoexpress.app.core.data.remote.ongoingtrip.toOngoingTrip
import com.cargoexpress.app.core.data.remote.ongoingtrip.toOngoingTripDtoPost
import com.cargoexpress.app.core.data.remote.ongoingtrip.toOngoingTripUpdateDto
import com.cargoexpress.app.core.domain.OngoingTrip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OngoingTripRepository(private val ongoingTripService: OngoingTripService) {

    suspend fun getOngoingTrips(token: String): Resource<List<OngoingTrip>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = ongoingTripService.getOngoingTrips("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toOngoingTrip() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch ongoing trips")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun createOngoingTrip(token: String, ongoingTrip: OngoingTrip): Resource<OngoingTrip> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = ongoingTripService.createOngoingTrip("Bearer $token", ongoingTrip.toOngoingTripDtoPost())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toOngoingTrip() ?: ongoingTrip)
            } else {
                Resource.Error(message = "Failed to create ongoing trip")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateOngoingTrip(token: String, id: Int, ongoingTrip: OngoingTrip): Resource<OngoingTrip> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = ongoingTripService.updateOngoingTrip(id, "Bearer $token", ongoingTrip.toOngoingTripUpdateDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toOngoingTrip() ?: ongoingTrip)
            } else {
                Resource.Error(message = "Failed to update ongoing trip")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}
