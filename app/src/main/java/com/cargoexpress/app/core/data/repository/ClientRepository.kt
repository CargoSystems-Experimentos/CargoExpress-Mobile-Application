package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.user.ClientDto
import com.cargoexpress.app.core.data.remote.user.ClientService

class ClientRepository(private val clientService: ClientService) {

    suspend fun getAllClients(token: String): Result<List<ClientDto>> {
        return try {
            val response = clientService.getAllClients("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error obteniendo clientes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClient(id: Int, token: String): Result<ClientDto> {
        return try {
            val response = clientService.getClient(id, "Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error obteniendo cliente: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClientByDni(dni: String, token: String): Result<ClientDto> {
        return try {
            val response = clientService.getClientByDni(dni, "Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Cliente no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
