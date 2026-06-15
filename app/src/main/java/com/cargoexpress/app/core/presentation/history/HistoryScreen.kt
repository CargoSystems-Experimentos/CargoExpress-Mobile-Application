package com.cargoexpress.app.core.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.common.SelectedLogHolder
import com.cargoexpress.app.core.data.repository.AuditLogRepository
import com.cargoexpress.app.core.domain.AuditLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    auditLogRepository: AuditLogRepository,
    navController: NavController
) {
    val factory = remember { HistoryViewModelFactory(auditLogRepository) }
    val viewModel: HistoryViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.destination?.route == Routes.History.routes) {
            viewModel.loadLogs()
        }
    }

    var selectedAction by remember { mutableStateOf("") }
    var selectedEntity by remember { mutableStateOf("") }
    var fromDateMillis by remember { mutableStateOf<Long?>(null) }
    var toDateMillis by remember { mutableStateOf<Long?>(null) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }
    var sortNewestFirst by remember { mutableStateOf(true) }

    val dateDisplayFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val allLogs = uiState.data ?: emptyList()
    val displayedLogs = remember(allLogs, selectedAction, selectedEntity, fromDateMillis, toDateMillis, sortNewestFirst) {
        allLogs
            .filter { log ->
                val actionMatch = selectedAction.isBlank() || log.action.uppercase() == selectedAction
                val entityMatch = selectedEntity.isBlank() || entityTypeLabel(log.entityType) == entityTypeLabel(selectedEntity)
                val dateMatch = run {
                    val logMs = parseTimestampToMillis(log.timestamp)
                    val from = fromDateMillis
                    val to = toDateMillis?.plus(86399999L)
                    when {
                        from != null && to != null -> logMs != null && logMs in from..to
                        from != null -> logMs != null && logMs >= from
                        to != null -> logMs != null && logMs <= to
                        else -> true
                    }
                }
                actionMatch && entityMatch && dateMatch
            }
            .let { list ->
                if (sortNewestFirst) list.sortedByDescending { parseTimestampToMillis(it.timestamp) ?: 0L }
                else list.sortedBy { parseTimestampToMillis(it.timestamp) ?: 0L }
            }
    }

    if (showFromPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = fromDateMillis)
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = { fromDateMillis = state.selectedDateMillis; showFromPicker = false }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showFromPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = state) }
    }

    if (showToPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = toDateMillis)
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = { toDateMillis = state.selectedDateMillis; showToPicker = false }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showToPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = state) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "HISTORIAL",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Date filter row
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showFromPicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (fromDateMillis == null) "Desde" else dateDisplayFormat.format(Date(fromDateMillis!!)),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp),
                        maxLines = 1
                    )
                }
                OutlinedButton(
                    onClick = { showToPicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (toDateMillis == null) "Hasta" else dateDisplayFormat.format(Date(toDateMillis!!)),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp),
                        maxLines = 1
                    )
                }
                if (fromDateMillis != null || toDateMillis != null) {
                    IconButton(onClick = { fromDateMillis = null; toDateMillis = null }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Limpiar fechas")
                    }
                }
                FilterChip(
                    selected = true,
                    onClick = { sortNewestFirst = !sortNewestFirst },
                    label = {
                        Text(
                            if (sortNewestFirst) "↓ Reciente" else "↑ Antiguo",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 11.sp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFEB3B))
                )
            }
        }

        // Action chips (first filter)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("" to "TODOS", "CREATE" to "CREAR", "UPDATE" to "ACTUALIZAR", "DELETE" to "ELIMINAR").forEach { (value, label) ->
                FilterChip(
                    selected = selectedAction == value,
                    onClick = { selectedAction = value },
                    label = { Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFEB3B))
                )
            }
        }

        // Entity type chips (second filter)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("" to "TODOS", "VEHICLE" to "VEHICULO", "DRIVER" to "CONDUCTOR", "TRIP" to "VIAJE", "ALERT" to "ALERTA", "EXPENSE" to "GASTO").forEach { (value, label) ->
                FilterChip(
                    selected = selectedEntity == value,
                    onClick = { selectedEntity = value },
                    label = { Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFEB3B))
                )
            }
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }
            uiState.message.isNotBlank() -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (displayedLogs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay registros en el historial.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(displayedLogs) { log ->
                            HistoryLogCard(
                                log = log,
                                onSeeMore = {
                                    SelectedLogHolder.log = log
                                    navController.navigate("history_detail")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryLogCard(
    log: AuditLog,
    onSeeMore: () -> Unit
) {
    val (actionBg, actionText, actionLabel) = actionStyle(log.action)
    val entityLabel = entityTypeLabel(log.entityType)
    val entityIcon = entityTypeIcon(log.entityType)
    val entityName = extractEntityName(log)
    val changeDescription = buildChangeDescription(log, entityLabel, entityName)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = entityIcon,
                        contentDescription = null,
                        tint = Color(0xFFF9A825),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entityLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatTimestamp(log.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = actionBg) {
                    Text(
                        text = actionLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = actionText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = changeDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (log.action != "DELETE") {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSeeMore,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Ver más",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp)
                    )
                }
            }
        }
    }
}

private fun actionStyle(action: String): Triple<Color, Color, String> = when (action) {
    "CREATE" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "CREAR")
    "UPDATE" -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "ACTUALIZAR")
    "DELETE" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "ELIMINAR")
    else -> Triple(Color(0xFFF5F5F5), Color(0xFF616161), action)
}

fun entityTypeLabel(entityType: String): String = when (entityType.uppercase()) {
    "VEHICLE", "VEHICLES" -> "VEHICULO"
    "DRIVER", "DRIVERS" -> "CONDUCTOR"
    "TRIP", "TRIPS" -> "VIAJE"
    "ALERT", "ALERTS" -> "ALERTA"
    "EXPENSE", "EXPENSES" -> "GASTO"
    else -> entityType
}

private fun entityTypeIcon(entityType: String): ImageVector = when (entityType.uppercase()) {
    "VEHICLE" -> Icons.Filled.DirectionsBus
    "DRIVER" -> Icons.Filled.Person
    "TRIP" -> Icons.Filled.LocalShipping
    "ALERT" -> Icons.Filled.Warning
    "EXPENSE" -> Icons.Filled.AttachMoney
    else -> Icons.Filled.Info
}

fun extractEntityName(log: AuditLog): String {
    val fields = log.modifiedFields
    return when (log.action) {
        "UPDATE" -> {
            val after = safeMap(fields["after"])
            val before = safeMap(fields["before"])
            extractNameFromFields(after ?: before ?: fields, log.entityType)
        }
        else -> extractNameFromFields(fields, log.entityType)
    }
}

private fun extractNameFromFields(map: Map<*, *>, entityType: String): String {
    return when (entityType.uppercase()) {
        "VEHICLE", "VEHICLES" -> map["Name"] as? String ?: map["Plate"] as? String
            ?: map["name"] as? String ?: map["plate"] as? String ?: ""
        "DRIVER", "DRIVERS" -> (map["Name"] as? String ?: map["name"] as? String ?: "").trim()
        "TRIP", "TRIPS" -> map["Name"] as? String ?: map["name"] as? String ?: ""
        "ALERT", "ALERTS" -> map["Title"] as? String ?: map["title"] as? String ?: ""
        "EXPENSE", "EXPENSES" -> map["FuelDescription"] as? String
            ?: map["fuelDescription"] as? String
            ?: map["FuelAmount"]?.toString() ?: ""
        else -> map["Name"] as? String ?: map["name"] as? String ?: ""
    }
}

@Suppress("UNCHECKED_CAST")
fun safeMap(value: Any?): Map<String, Any?>? = value as? Map<String, Any?>

private fun buildChangeDescription(log: AuditLog, entityLabel: String, name: String): String {
    val nameStr = if (name.isNotBlank()) " $name" else ""
    return when (log.action) {
        "CREATE" -> "Se ha creado un nuevo ${entityLabel.lowercase()}."
        "UPDATE" -> "${entityLabel}$nameStr ha sido actualizado."
        "DELETE" -> "${entityLabel}$nameStr ha sido eliminado. (Desactivado)"
        else -> "${entityLabel}$nameStr fue modificado."
    }
}

fun formatTimestamp(timestamp: String): String {
    val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss"
    )
    val out = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    for (pattern in formats) {
        try {
            val parsed = SimpleDateFormat(pattern, Locale.getDefault())
                .apply { isLenient = false }
                .parse(timestamp)
            if (parsed != null) return out.format(parsed)
        } catch (_: Exception) {}
    }
    return timestamp
}

fun parseTimestampToMillis(timestamp: String): Long? {
    val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss"
    )
    for (pattern in formats) {
        try {
            return SimpleDateFormat(pattern, Locale.getDefault())
                .apply { isLenient = false }
                .parse(timestamp)?.time
        } catch (_: Exception) {}
    }
    return null
}
