package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.driver.DriverService
import com.cargoexpress.app.core.data.remote.driver.DriverStateUpdateDto
import com.cargoexpress.app.core.data.remote.driver.DriverUpdateDto
import com.cargoexpress.app.core.data.remote.driver.toDriver
import com.cargoexpress.app.core.data.remote.driver.toDriverPostDto
import com.cargoexpress.app.core.domain.Driver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriverRepository(private val driverService: DriverService) {

    suspend fun getDrivers(token: String, entrepreneurId: Int): Resource<List<Driver>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = driverService.getDriversByEntrepreneur(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toDriver() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch drivers: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getDriverById(driverId: Int): Resource<Driver> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = driverService.getDriverById(driverId, "Bearer ${Constants.TOKEN}")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toDriver())
            } else {
                Resource.Error(message = "No se pudo obtener el conductor")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun addDriver(driver: Driver): Resource<Driver> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = driverService.addDriver("Bearer ${Constants.TOKEN}", driver.toDriverPostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toDriver() ?: driver)
            } else {
                Resource.Error(message = "Failed to add driver")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateDriver(driverId: Int, name: String, contactNumber: String): Resource<DriverUpdateDto> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = driverService.updateDriver(driverId, "Bearer ${Constants.TOKEN}", DriverUpdateDto(name, contactNumber))
            if (response.isSuccessful) {
                Resource.Success(data = response.body() ?: DriverUpdateDto(name, contactNumber))
            } else {
                val errorBody = response.errorBody()?.string()
                Resource.Error(message = errorBody ?: "No se pudo actualizar el conductor")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateDriverState(driverId: Int, state: String): Resource<DriverStateUpdateDto> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = driverService.updateDriverState(driverId, "Bearer ${Constants.TOKEN}", DriverStateUpdateDto(state))
            if (response.isSuccessful) {
                Resource.Success(data = response.body() ?: DriverStateUpdateDto(state))
            } else {
                val errorBody = response.errorBody()?.string()
                Resource.Error(message = errorBody ?: "No se pudo actualizar el estado")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}
