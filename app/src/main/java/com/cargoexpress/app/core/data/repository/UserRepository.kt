package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.user.UserDto
import com.cargoexpress.app.core.data.remote.user.UserService
import com.cargoexpress.app.core.data.remote.user.toUser
import com.cargoexpress.app.core.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userService: UserService) {

    suspend fun getUser(userId: Int, token: String): Resource<User> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = userService.getUser(userId, "Bearer $token")
            if (response.isSuccessful) {
                val user = response.body()?.toUser()
                if (user != null) Resource.Success(data = user)
                else Resource.Error(message = "Response body is null")
            } else {
                Resource.Error(message = "Failed to fetch user: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getUserRole(userId: Int, token: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = userService.getUserRole(userId, "Bearer $token")
            if (response.isSuccessful) {
                val role = response.body()?.role
                if (role != null) Resource.Success(data = role)
                else Resource.Error(message = "Response body is null")
            } else {
                Resource.Error(message = "Failed to fetch user role: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}
