package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.vehicle.VehicleNameUpdateDto
import com.cargoexpress.app.core.data.remote.vehicle.VehicleService
import com.cargoexpress.app.core.data.remote.vehicle.VehicleStateUpdateDto
import com.cargoexpress.app.core.data.remote.vehicle.toVehicle
import com.cargoexpress.app.core.data.remote.vehicle.toVehiclePostDto
import com.cargoexpress.app.core.domain.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VehicleRepository(private val vehicleService: VehicleService) {

    suspend fun getVehicleList(token: String, entrepreneurId: Int): Resource<List<Vehicle>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = vehicleService.getVehiclesByEntrepreneur(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toVehicle() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch vehicles: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getVehicleById(vehicleId: Int): Resource<Vehicle> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = vehicleService.getVehicleById(vehicleId, "Bearer ${Constants.TOKEN}")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()!!.toVehicle())
            } else {
                Resource.Error(message = parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun addVehicle(vehicle: Vehicle): Resource<Vehicle> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = vehicleService.addVehicle("Bearer ${Constants.TOKEN}", vehicle.toVehiclePostDto())
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.toVehicle() ?: vehicle)
            } else {
                Resource.Error(message = parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateVehicleName(vehicleId: Int, name: String): Resource<VehicleNameUpdateDto> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = vehicleService.updateVehicleName(vehicleId, "Bearer ${Constants.TOKEN}", VehicleNameUpdateDto(name))
            if (response.isSuccessful) {
                Resource.Success(data = response.body()!!)
            } else {
                Resource.Error(message = parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateVehicleState(vehicleId: Int, state: String): Resource<VehicleStateUpdateDto> = withContext(Dispatchers.IO) {
        if (Constants.TOKEN.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = vehicleService.updateVehicleState(vehicleId, "Bearer ${Constants.TOKEN}", VehicleStateUpdateDto(state))
            if (response.isSuccessful) {
                Resource.Success(data = response.body()!!)
            } else {
                Resource.Error(message = parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    private fun parseError(body: String?): String {
        if (body.isNullOrBlank()) return "Error desconocido"
        return try {
            org.json.JSONObject(body).optString("message", body)
        } catch (_: Exception) { body }
    }
}
