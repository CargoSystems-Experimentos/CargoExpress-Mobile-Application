package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.alert.AlertService
import com.cargoexpress.app.core.data.remote.alert.toAlert
import com.cargoexpress.app.core.domain.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cargoexpress.app.core.common.Resource

class AlertRepository(private val alertService: AlertService) {
    suspend fun getAlerts(token: String): Resource<List<Alert>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) {
            println("Token is required")
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val response = alertService.getAlerts("Bearer $token")
            println("API response: $response")
            if (response.isSuccessful) {
                val alerts = response.body()?.map { it.toAlert() } ?: emptyList()
                println("Mapped alerts: $alerts")
                Resource.Success(data = alerts)
            } else {
                println("Failed to fetch alerts: ${response.message()}")
                Resource.Error(message = "Failed to fetch alerts")
            }
        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}