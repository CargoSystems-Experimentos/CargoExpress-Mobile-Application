package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.ongoingtrip.OngoingTripService
import com.cargoexpress.app.core.data.remote.ongoingtrip.OngoingTripStateUpdateDto
import com.cargoexpress.app.core.data.remote.ongoingtrip.toOngoingTrip
import com.cargoexpress.app.core.data.remote.ongoingtrip.toOngoingTripDtoPost
import com.cargoexpress.app.core.domain.OngoingTrip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cargoexpress.app.core.common.Resource

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

    suspend fun finalizeOngoingTrip(token: String, id: Int): Resource<OngoingTrip> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = ongoingTripService.updateOngoingTripState(id, "Bearer $token", OngoingTripStateUpdateDto("FINALIZADO"))
            if (response.isSuccessful) {
                val ongoingTrip = response.body()?.toOngoingTrip()
                if (ongoingTrip != null) {
                    Resource.Success(data = ongoingTrip)
                } else {
                    Resource.Error(message = "Response body is null")
                }
            } else {
                Resource.Error(message = "Failed to finalize trip")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}
