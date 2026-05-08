package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.data.remote.user.ClientDto
import com.cargoexpress.app.core.data.remote.user.ClientRequestDto
import com.cargoexpress.app.core.data.remote.user.ClientService

class ClientRepository(private val clientService: ClientService) {


    suspend fun createClient(request: ClientRequestDto, token: String): Result<ClientDto> {
        return try {
            val response = clientService.createClient(request, "Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error creando cliente: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClient(id: Int, token: String): Result<ClientDto> {
        return try {
            val response = clientService.getClient(id, token)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error obteniendo cliente: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}