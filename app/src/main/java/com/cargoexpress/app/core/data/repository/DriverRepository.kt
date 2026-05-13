package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.driver.DriverService
import com.cargoexpress.app.core.data.remote.driver.toDriver
import com.cargoexpress.app.core.data.remote.driver.toDriverDto
import com.cargoexpress.app.core.domain.Driver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource

class DriverRepository(private val driverService: DriverService) {

    suspend fun getDrivers(token: String, entrepreneurId: Int): Resource<List<Driver>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val bearerToken = "Bearer $token"
            val primaryResponse = driverService.getDrivers(bearerToken, entrepreneurId)

            val primaryDrivers = if (primaryResponse.isSuccessful) {
                primaryResponse.body()?.map { it.toDriver() } ?: emptyList()
            } else {
                emptyList()
            }

            if (primaryDrivers.isNotEmpty()) {
                Resource.Success(data = primaryDrivers)
            } else {
                val fallbackResponse = driverService.getDriversDirectByEntrepreneur(bearerToken, entrepreneurId)
                if (fallbackResponse.isSuccessful) {
                    val fallbackDrivers = fallbackResponse.body()?.map { it.toDriver() } ?: emptyList()
                    Resource.Success(data = fallbackDrivers)
                } else {
                    Resource.Error(message = "Failed to fetch drivers: ${fallbackResponse.code()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun addDriver(driver: Driver): Resource<Driver> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val response = driverService.addDriver("Bearer ${Constants.TOKEN}", driver.toDriverDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toDriver() ?: driver)
            } else {
                Resource.Error(message = "Failed to add driver")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}