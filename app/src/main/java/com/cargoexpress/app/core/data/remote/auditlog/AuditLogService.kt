package com.cargoexpress.app.core.data.remote.auditlog

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AuditLogService {
    @GET("audit-logs")
    suspend fun getAuditLogs(
        @Header("Authorization") token: String
    ): Response<List<AuditLogDto>>

    @GET("audit-logs/{auditLogId}")
    suspend fun getAuditLogById(
        @Path("auditLogId") auditLogId: String,
        @Header("Authorization") token: String
    ): Response<AuditLogDto>

    @GET("audit-logs/entrepreneur/{entrepreneurId}")
    suspend fun getAuditLogsByEntrepreneur(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<AuditLogDto>>

    @GET("audit-logs/entrepreneur/alerts/{entrepreneurId}")
    suspend fun getAlertAuditLogs(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<AuditLogDto>>

    @GET("audit-logs/entrepreneur/expenses/{entrepreneurId}")
    suspend fun getExpenseAuditLogs(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<AuditLogDto>>

    @GET("audit-logs/entrepreneur/drivers/{entrepreneurId}")
    suspend fun getDriverAuditLogs(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<AuditLogDto>>

    @GET("audit-logs/entrepreneur/trips/{entrepreneurId}")
    suspend fun getTripAuditLogs(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<AuditLogDto>>

    @GET("audit-logs/entrepreneur/vehicles/{entrepreneurId}")
    suspend fun getVehicleAuditLogs(
        @Path("entrepreneurId") entrepreneurId: Int,
        @Header("Authorization") token: String
    ): Response<List<AuditLogDto>>
}
