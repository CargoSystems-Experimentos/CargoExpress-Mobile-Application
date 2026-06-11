package com.cargoexpress.app.core.data.repository

import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.auditlog.AuditLogService
import com.cargoexpress.app.core.data.remote.auditlog.toAuditLog
import com.cargoexpress.app.core.domain.AuditLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuditLogRepository(private val auditLogService: AuditLogService) {

    suspend fun getAuditLogsByEntrepreneur(token: String, entrepreneurId: Int): Resource<List<AuditLog>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = auditLogService.getAuditLogsByEntrepreneur(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAuditLog() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch audit logs")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getTripAuditLogs(token: String, entrepreneurId: Int): Resource<List<AuditLog>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = auditLogService.getTripAuditLogs(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAuditLog() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch trip audit logs")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getAlertAuditLogs(token: String, entrepreneurId: Int): Resource<List<AuditLog>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = auditLogService.getAlertAuditLogs(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAuditLog() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch alert audit logs")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getExpenseAuditLogs(token: String, entrepreneurId: Int): Resource<List<AuditLog>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = auditLogService.getExpenseAuditLogs(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAuditLog() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch expense audit logs")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getDriverAuditLogs(token: String, entrepreneurId: Int): Resource<List<AuditLog>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = auditLogService.getDriverAuditLogs(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAuditLog() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch driver audit logs")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getVehicleAuditLogs(token: String, entrepreneurId: Int): Resource<List<AuditLog>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) return@withContext Resource.Error(message = "Token is required")
        return@withContext try {
            val response = auditLogService.getVehicleAuditLogs(entrepreneurId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(data = response.body()?.map { it.toAuditLog() } ?: emptyList())
            } else {
                Resource.Error(message = "Failed to fetch vehicle audit logs")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An unknown error occurred")
        }
    }
}
