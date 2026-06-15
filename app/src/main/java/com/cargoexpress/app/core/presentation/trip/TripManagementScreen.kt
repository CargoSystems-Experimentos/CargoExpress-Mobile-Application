package com.cargoexpress.app.core.presentation.trip

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Trip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripManagementScreen(
    tripRepository: TripRepository,
    ongoingTripRepository: OngoingTripRepository,
    navController: NavController
) {
    val factory = remember { TripManagementViewModelFactory(tripRepository, ongoingTripRepository) }
    val viewModel: TripManagementViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        val route = navBackStackEntry?.destination?.route
        if (route == "trips" || route == Routes.TripList.routes) {
            viewModel.loadTrips()
        }
    }

    var nameQuery by remember { mutableStateOf("") }
    var appliedNameQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("EN ESPERA") }
    var sortAscending by remember { mutableStateOf(true) }
    var fromDateMillis by remember { mutableStateOf<Long?>(null) }
    var toDateMillis by remember { mutableStateOf<Long?>(null) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }
    var showExtraFilters by remember { mutableStateOf(false) }

    val tripTypes = listOf("ESTANDAR", "FRAGIL", "PESADO", "VALIOSO", "URGENTE", "PERECIBLE")
    val dateDisplayFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val allTrips = uiState.data ?: emptyList()
    val displayedTrips = remember(allTrips, appliedNameQuery, selectedType, selectedStatus, fromDateMillis, toDateMillis, sortAscending) {
        allTrips
            .filter { trip ->
                val nameMatch = appliedNameQuery.isBlank() || trip.name.contains(appliedNameQuery, ignoreCase = true)
                val typeMatch = selectedType.isBlank() || trip.type == selectedType
                val statusMatch = tripStateToLabel(trip.state) == selectedStatus
                val dateMatch = run {
                    val loadMs = parseIsoToMillis(trip.loadDate)
                    val from = fromDateMillis
                    val to = toDateMillis?.plus(86399999L)
                    when {
                        from != null && to != null -> loadMs != null && loadMs in from..to
                        from != null -> loadMs != null && loadMs >= from
                        to != null -> loadMs != null && loadMs <= to
                        else -> true
                    }
                }
                nameMatch && typeMatch && statusMatch && dateMatch
            }
            .let {
                if (sortAscending) it.sortedBy { t -> t.name.lowercase() }
                else it.sortedByDescending { t -> t.name.lowercase() }
            }
    }

    if (showFromPicker) {
        val fromPickerState = rememberDatePickerState(initialSelectedDateMillis = fromDateMillis)
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = { fromDateMillis = fromPickerState.selectedDateMillis; showFromPicker = false }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showFromPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = fromPickerState) }
    }

    if (showToPicker) {
        val toPickerState = rememberDatePickerState(initialSelectedDateMillis = toDateMillis)
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = { toDateMillis = toPickerState.selectedDateMillis; showToPicker = false }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showToPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = toPickerState) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "MIS VIAJES",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nameQuery,
                    onValueChange = { nameQuery = it },
                    label = { Text("Buscar por nombre") },
                    leadingIcon = { Icon(Icons.Filled.LocalShipping, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { appliedNameQuery = nameQuery },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFFEB3B))
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Black)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = true,
                    onClick = { sortAscending = !sortAscending },
                    label = { Text((if (sortAscending) "↑ A-Z" else "↓ Z-A"), style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp))},
                    leadingIcon = {
                        Icon(
                            if (sortAscending) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFEB3B)
                    )
                )
                val hasActiveFilters = selectedType.isNotBlank() || fromDateMillis != null || toDateMillis != null
                FilterChip(
                    selected = showExtraFilters || hasActiveFilters,
                    onClick = { showExtraFilters = !showExtraFilters },
                    label = { Text("FILTROS", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)) },
                    trailingIcon = {
                        Icon(
                            if (showExtraFilters) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            if (showExtraFilters) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = if (selectedType.isBlank()) "Tipo: Todos" else "Tipo: $selectedType",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
                            )
                            ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                                DropdownMenuItem(text = { Text("Todos") }, onClick = { selectedType = ""; typeExpanded = false })
                                tripTypes.forEach { t ->
                                    DropdownMenuItem(text = { Text(t) }, onClick = { selectedType = t; typeExpanded = false })
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("EN ESPERA", "EN PROGRESO", "FINALIZADO", "CANCELADO").forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status },
                        label = { Text(status, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFEB3B)
                        ),

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
                    TripList(trips = displayedTrips, navController = navController)
                }
            }
        }

        if (Constants.USER_ROLE != "CLIENT") {
            FloatingActionButton(
                onClick = { navController.navigate("register_trip") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .shadow(elevation = 20.dp)
                ,
                containerColor = Color(0xFFFFEB3B),

            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar viaje", tint = Color.Black)
            }
        }
    }
}

@Composable
fun TripList(
    trips: List<Trip>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (trips.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron viajes\nNada por aqui...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(trips) { trip ->
                TripCard(trip = trip, navController = navController)
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tripTypeIcon(trip.type),
                        contentDescription = null,
                        tint = Color(0xFFF9A825),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = trip.type.ifBlank { "-" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TripStatusChip(trip.state)
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            //TripInfoItem(icon = Icons.Filled.LocationOn, label = "Origen", value = trip.loadLocation)
            TripInfoItem(icon = Icons.Filled.Place, label = "Destino", value = trip.unloadLocation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TripInfoItem(
                    icon = Icons.Filled.DateRange,
                    label = "Fecha carga",
                    value = formatDateTimeReadable(trip.loadDate),
                    modifier = Modifier.weight(1f)
                )
                TripInfoItem(
                    icon = Icons.Filled.DateRange,
                    label = "Fecha descarga",
                    value = formatDateTimeReadable(trip.unloadDate),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate("trip_details/${trip.id}") },
                    modifier = Modifier.width(120.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Detalle", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge.copy(fontSize=15.sp))
                }
                Button(
                    onClick = {
                        navController.navigate("gps/${trip.id}")
                        Constants.TRIP_ID = trip.id
                    },
                    modifier = Modifier.width(120.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("GPS", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge.copy(fontSize=15.sp))
                }
            }
        }
    }
}

private fun tripTypeIcon(type: String): ImageVector = when (type.uppercase()) {
    "FRAGIL" -> Icons.Filled.Warning
    "PESADO" -> Icons.Filled.Scale
    "VALIOSO" -> Icons.Filled.Star
    "URGENTE" -> Icons.Filled.Notifications
    "PERECIBLE" -> Icons.Filled.Eco
    else -> Icons.Filled.LocalShipping
}

@Composable
fun TripStatusChip(state: String) {
    val label = tripStateToLabel(state)
    val (bgColor, textColor) = when (state) {
        "PROGRESS" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "FINISHED" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "CANCELED" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        else -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bgColor) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TripInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 6.dp), // <- usar modifier recibido
        verticalAlignment = Alignment.Top // mejor para dos columnas con texto de distinto alto
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}

fun tripStateToLabel(state: String) = when (state) {
    "AWAITING" -> "EN ESPERA"
    "PROGRESS" -> "EN PROGRESO"
    "FINISHED" -> "FINALIZADO"
    "CANCELED" -> "CANCELADO"
    else -> state
}

private fun formatDateTimeReadable(dateTime: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val parsed = input.parse(dateTime)
        if (parsed != null) output.format(parsed) else dateTime
    } catch (e: Exception) {
        dateTime
    }
}

private fun parseIsoToMillis(isoDate: String): Long? {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(isoDate)?.time
    } catch (_: Exception) { null }
}
