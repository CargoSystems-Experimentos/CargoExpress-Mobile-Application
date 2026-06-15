package com.cargoexpress.app.core.domain

data class AuditLog(
    val id: String,
    val entityType: String,
    val action: String,
    val timestamp: String,
    val modifiedFields: Map<String, Any?>,
    val entrepreneurId: Int
)
