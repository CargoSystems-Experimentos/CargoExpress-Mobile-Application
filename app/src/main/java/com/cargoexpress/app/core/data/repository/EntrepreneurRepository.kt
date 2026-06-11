package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.data.remote.user.ClientDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurService
import com.cargoexpress.app.core.data.remote.vehicle.VehicleDto

class EntrepreneurRepository(private val entrepreneurService: EntrepreneurService) {

    suspend fun getEntrepreneurById(entrepreneurId: Int, token: String): Result<EntrepreneurDto> {
        return try {
            val response = entrepreneurService.getEntrepreneurById(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Error: No se pudo obtener el empresario"))
            } else {
                Result.failure(Exception("Error obteniendo empresario: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClientsByEntrepreneurId(entrepreneurId: Int, token: String): Result<List<ClientDto>> {
        return try {
            val response = entrepreneurService.getClientsByEntrepreneurId(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error obteniendo clientes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVehiclesByEntrepreneurId(id: Int, token: String): Result<List<VehicleDto>> {
        return try {
            val response = entrepreneurService.getVehiclesByEntrepreneurId(id, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Cuerpo de la respuesta vacío"))
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDriversByEntrepreneurId(entrepreneurId: Int, token: String): Resource<List<DriverDto>> {
        return try {
            val response = entrepreneurService.getDriversByEntrepreneurId(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Error fetching drivers")
            }
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message}")
        }
    }
}
