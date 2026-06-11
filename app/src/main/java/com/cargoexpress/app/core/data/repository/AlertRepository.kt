package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.alert.AlertService
import com.cargoexpress.app.core.data.remote.alert.toAlert
import com.cargoexpress.app.core.data.remote.alert.toAlertPostDto
import com.cargoexpress.app.core.domain.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlertRepository(private val alertService: AlertService) {

    suspend fun getAlerts(token: String): Resource<List<Alert>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = alertService.getAlerts("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAlert() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch alerts")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getAlertsByTripId(token: String, tripId: Int): Resource<List<Alert>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = alertService.getAlertsByTripId(tripId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAlert() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch alerts")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun createAlert(alert: Alert): Resource<Alert> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = alertService.createAlert("Bearer ${Constants.TOKEN}", alert.toAlertPostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toAlert() ?: alert)
            } else {
                Resource.Error(message = "Failed to create alert")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}
