package com.cargoexpress.app.core.data.repository

import android.util.Log
import com.cargoexpress.app.core.data.remote.user.EntrepreneurDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurRequestDto
import com.cargoexpress.app.core.data.remote.user.EntrepreneurService
import com.cargoexpress.app.core.data.remote.vehicle.VehicleDto
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.common.Resource

class EntrepreneurRepository(private val entrepreneurService: EntrepreneurService) {

    suspend fun createEntrepreneur(request: EntrepreneurRequestDto, token: String): Result<Int> {
        return try {
            val response = entrepreneurService.createEntrepreneur(request, "Bearer $token")
            if (response.isSuccessful) {
                val entrepreneur = response.body()
                entrepreneur?.let {
                    val entrepreneurId = it.id
                    Result.success(entrepreneurId)
                } ?: Result.failure(Exception("Error: No se pudo obtener el entrepreneurId"))
            } else {
                val errorMessage = ApiErrorParser.parse(response)
                Log.e("EntrepreneurRepository", "Error creando entrepreneur: code=${response.code()}, msg=$errorMessage")
                Result.failure(Exception("Error creando entrepreneur: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("EntrepreneurRepository", "Excepción creando entrepreneur", e)
            Result.failure(e)
        }
    }

    suspend fun getEntrepreneurByUserId(userId: Int, token: String): Result<EntrepreneurDto> {
        return try {
            val response = entrepreneurService.getEntrepreneurByUserId(userId, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error: No se pudo obtener el empresario"))
            } else {
                Result.failure(Exception("Error obteniendo empresario: ${ApiErrorParser.parse(response)}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEntrepreneurById(entrepreneurId: Int, token: String): Result<EntrepreneurDto> {
        return try {
            val response = entrepreneurService.getEntrepreneurById(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error: No se pudo obtener el empresario"))
            } else {
                Result.failure(Exception("Error obteniendo empresario: ${ApiErrorParser.parse(response)}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVehiclesByEntrepreneurId(id: Int, token: String): Result<List<VehicleDto>> {
        return try {
            val response = entrepreneurService.getVehiclesEntrepreneurs(id, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Cuerpo de la respuesta vacío"))
            } else {
                Result.failure(Exception(ApiErrorParser.parse(response)))
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
