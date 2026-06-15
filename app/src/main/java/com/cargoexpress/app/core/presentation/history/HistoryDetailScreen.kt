package com.cargoexpress.app.core.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.SelectedLogHolder
import com.cargoexpress.app.core.domain.AuditLog

private fun shouldHideField(key: String): Boolean {
    if (key.equals("id", ignoreCase = true)) return true
    if (key.endsWith("Id") || key.endsWith("_id")) return true
    if (key in setOf("createdAt", "updatedAt", "created_at", "updated_at", "CreatedAt", "UpdatedAt")) return true
    return false
}

private val FIELD_NAME_ES = mapOf(
    // Vehículo
    "Name" to "Nombre",
    "Model" to "Modelo",
    "Plate" to "Placa",
    "TractorPlate" to "Placa de tracción",
    "MaxLoad" to "Carga máxima",
    "Volume" to "Volumen",
    "State" to "Estado",
    // Conductor
    "Dni" to "DNI",
    "License" to "Licencia",
    "ContactNumber" to "Número de contacto",
    // Viaje
    "Type" to "Tipo",
    "Weight" to "Peso",
    "LoadLocation" to "Lugar de carga",
    "LoadDate" to "Fecha de carga",
    "UnloadLocation" to "Lugar de descarga",
    "UnloadDate" to "Fecha de descarga",
    // Alerta
    "Title" to "Título",
    "Description" to "Descripción",
    "Date" to "Fecha",
    // Gasto
    "FuelAmount" to "Monto de combustible",
    "FuelDescription" to "Descripción de combustible",
    "ViaticsAmount" to "Monto de viáticos",
    "ViaticsDescription" to "Descripción de viáticos",
    "TollsAmount" to "Monto de peajes",
    "TollsDescription" to "Descripción de peajes"
)

@Composable
fun HistoryDetailScreen(navController: NavController) {
    val log = SelectedLogHolder.log

    if (log == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontró el registro.")
        }
        return
    }

    val entityLabel = entityTypeLabel(log.entityType)
    val entityName = extractEntityName(log)
    val title = if (entityName.isNotBlank()) "$entityLabel $entityName" else entityLabel

    Column(modifier = Modifier.fillMaxSize()) {
        // Custom top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 40.dp, bottom = 8.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 56.dp),
                maxLines = 1
            )
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Action + date header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (bg, textColor, label) = when (log.action) {
                    "CREATE" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "CREAR")
                    "UPDATE" -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "ACTUALIZAR")
                    else -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "ELIMINAR")
                }
                Surface(shape = RoundedCornerShape(8.dp), color = bg) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = formatTimestamp(log.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            when (log.action) {
                "CREATE" -> CreateDetailContent(log)
                "UPDATE" -> UpdateDetailContent(log)
            }
        }
    }
}

@Composable
private fun CreateDetailContent(log: AuditLog) {
    Text(
        text = "Datos del registro",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(bottom = 4.dp)
    )
    val fields = log.modifiedFields.filter { (k, _) -> !shouldHideField(k) }
    FieldTable(fields)
}

@Composable
private fun UpdateDetailContent(log: AuditLog) {
    val fields = log.modifiedFields
    val beforeMap = safeMap(fields["before"])
    val afterMap = safeMap(fields["after"])

    if (beforeMap != null && afterMap != null) {
        // Before/After structure
        val allKeys = (beforeMap.keys + afterMap.keys)
            .filter { !shouldHideField(it) }
            .distinct()

        Text(
            text = "Cambios realizados",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        allKeys.forEach { key ->
            val before = beforeMap[key]?.toString() ?: "-"
            val after = afterMap[key]?.toString() ?: "-"
            if (before != after) {
                BeforeAfterRow(
                    fieldName = formatFieldName(key),
                    before = before,
                    after = after
                )
            }
        }

        val unchanged = allKeys.filter { key ->
            beforeMap[key]?.toString() == afterMap[key]?.toString()
        }
        if (unchanged.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Campos sin cambios",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            unchanged.forEach { key ->
                FieldRow(
                    label = formatFieldName(key),
                    value = beforeMap[key]?.toString() ?: "-"
                )
            }
        }
    } else {
        // Flat structure — treat as "after" values only
        Text(
            text = "Valores actualizados",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        val flat = fields.filter { (k, _) -> !shouldHideField(k) }
        FieldTable(flat)
    }
}

@Composable
private fun FieldTable(fields: Map<String, Any?>) {
    fields.forEach { (key, value) ->
        FieldRow(label = formatFieldName(key), value = value?.toString() ?: "-")
    }
}

@Composable
private fun FieldRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun BeforeAfterRow(fieldName: String, before: String, after: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = fieldName,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Antes",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFFC62828)
                    )
                    Text(
                        text = before,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Después",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = after,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun formatFieldName(key: String): String {
    return FIELD_NAME_ES[key] ?: key
        .replace(Regex("([A-Z])")) { " ${it.value}" }
        .replace("_", " ")
        .replaceFirstChar { it.uppercaseChar() }
        .trim()
}
