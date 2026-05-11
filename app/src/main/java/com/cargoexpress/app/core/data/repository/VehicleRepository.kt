package com.cargoexpress.app.core.data.repository



import com.cargoexpress.app.core.data.remote.vehicle.VehicleService
import com.cargoexpress.app.core.data.remote.vehicle.toVehicle
import com.cargoexpress.app.core.data.remote.vehicle.toVehicleDto
import com.cargoexpress.app.core.domain.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource

class VehicleRepository(private val vehicleService: VehicleService) {

    suspend fun getVehicleList(token: String, entrepreneurId: Int): Resource<List<Vehicle>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        val bearerToken = "Bearer $token"
        val primaryResponse = vehicleService.getVehicles(bearerToken, entrepreneurId)

        val primaryVehicles = if (primaryResponse.isSuccessful) {
            primaryResponse.body()?.map { it.toVehicle() } ?: emptyList()
        } else {
            emptyList()
        }

        if (primaryVehicles.isNotEmpty()) {
            return@withContext Resource.Success(primaryVehicles)
        }

        val fallbackResponse = vehicleService.getVehiclesDirectByEntrepreneur(bearerToken, entrepreneurId)
        if (fallbackResponse.isSuccessful) {
            val fallbackVehicles = fallbackResponse.body()?.map { it.toVehicle() } ?: emptyList()
            return@withContext Resource.Success(fallbackVehicles)
        }

        return@withContext Resource.Error("Failed to fetch vehicles: ${fallbackResponse.code()}")
    }

    suspend fun addVehicle(vehicle: Vehicle): Resource<Vehicle> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) {
            return@withContext Resource.Error(message = "Token is required")
        }
        return@withContext try {
            val response = vehicleService.addVehicle("Bearer ${Constants.TOKEN}", vehicle.toVehicleDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toVehicle() ?: vehicle)
            } else {
                Resource.Error(message = "Failed to add vehicle")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}