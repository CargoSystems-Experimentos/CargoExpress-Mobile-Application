package com.cargoexpress.app.core.data.remote.auditlog

import com.cargoexpress.app.core.domain.AuditLog

data class AuditLogDto(
    val id: String,
    val entityType: String,
    val action: String,
    val timestamp: String,
    val modifiedFields: Map<String, Any?>,
    val entrepreneurId: Int
)

fun AuditLogDto.toAuditLog() = AuditLog(
    id = id,
    entityType = entityType,
    action = action,
    timestamp = timestamp,
    modifiedFields = modifiedFields,
    entrepreneurId = entrepreneurId
)
