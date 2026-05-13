package com.cargoexpress.app.core.presentation.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.OngoingTrip
import com.cargoexpress.app.core.domain.Trip
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TripManagementScreen(
    tripRepository: TripRepository,
    ongoingTripRepository: OngoingTripRepository,
    navController: NavController
) {
    val factory = remember { TripManagementViewModelFactory(tripRepository, ongoingTripRepository) }
    val viewModel: TripManagementViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val ongoingTrips by viewModel.ongoingTrips.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Nombre") }
    var sortAscending by remember { mutableStateOf(true) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    val displayedTrips = remember(uiState.data, ongoingTrips, selectedStatus) {
        val base = uiState.data ?: emptyList()
        if (selectedStatus == null) base
        else base.filter { trip ->
            val ongoing = ongoingTrips.find { it.tripId == trip.id }
            val status = when {
                ongoing == null -> "SIN INICIAR"
                ongoing.state == "FINALIZADO" -> "FINALIZADO"
                else -> "EN PROGRESO"
            }
            status == selectedStatus
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadOngoingTrips(Constants.TOKEN)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Mis Viajes",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.updateSearchQuery(searchQuery, selectedFilter)
                },
                label = { Text("Buscar viaje") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedFilter == "Nombre",
                    onClick = {
                        selectedFilter = "Nombre"
                        viewModel.updateSearchQuery(searchQuery, selectedFilter)
                    },
                    label = { Text("Nombre") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )

                FilterChip(
                    selected = selectedFilter == "Tipo",
                    onClick = {
                        selectedFilter = "Tipo"
                        viewModel.updateSearchQuery(searchQuery, selectedFilter)
                    },
                    label = { Text("Tipo") },
                    leadingIcon = {
                        Icon(Icons.Filled.Category, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )

                FilterChip(
                    selected = selectedFilter == "Fecha",
                    onClick = {
                        selectedFilter = "Fecha"
                        viewModel.updateSearchQuery(searchQuery, selectedFilter)
                    },
                    label = { Text("Fecha") },
                    leadingIcon = {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                FilterChip(
                    selected = true,
                    onClick = { sortAscending = !sortAscending },
                    label = { Text(if (sortAscending) "↑ A-Z" else "↓ Z-A") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (sortAscending) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(null, "SIN INICIAR", "EN PROGRESO", "FINALIZADO").forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = if (selectedStatus == status) null else status },
                        label = { Text(status ?: "Todos") }
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
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
                    TripList(
                        trips = displayedTrips,
                        ongoingTrips = ongoingTrips,
                        navController = navController,
                        isDescending = !sortAscending
                    )
                }
            }
        }

        if (Constants.USER_ROLE != "CLIENT") {
            FloatingActionButton(
                onClick = { navController.navigate("register_trip") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFFFFEB3B)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar viaje", tint = Color.Black)
            }
        }
    }
}

@Composable
fun TripList(
    trips: List<Trip>,
    ongoingTrips: List<OngoingTrip>,
    navController: NavController,
    isDescending: Boolean
) {
    val sortedTrips = if (isDescending) {
        trips.sortedByDescending { it.id }
    } else {
        trips.sortedBy { it.id }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (sortedTrips.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron viajes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(sortedTrips) { trip ->
                val ongoing = ongoingTrips.find { it.tripId == trip.id }
                val status = when {
                    ongoing == null -> "SIN INICIAR"
                    ongoing.state == "FINALIZADO" -> "FINALIZADO"
                    else -> "EN PROGRESO"
                }
                TripCard(trip = trip, navController = navController, tripStatus = status)
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    navController: NavController,
    tripStatus: String = "SIN INICIAR"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
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
                TripStatusChip(tripStatus)
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            TripInfoItem(
                icon = Icons.Filled.LocationOn,
                label = "Origen",
                value = trip.loadLocation
            )
            TripInfoItem(
                icon = Icons.Filled.Place,
                label = "Destino",
                value = trip.unloadLocation
            )
            TripInfoItem(
                icon = Icons.Filled.DateRange,
                label = "Fecha carga",
                value = formatDateTimeReadable(trip.loadDate)
            )
            TripInfoItem(
                icon = Icons.Filled.DateRange,
                label = "Fecha descarga",
                value = formatDateTimeReadable(trip.unloadDate)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate("trip_details/${trip.id}") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
                ) {
                    Text("Detalle", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        navController.navigate("gps/${trip.id}")
                        Constants.TRIP_ID = trip.id
                              },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
                ) {
                    Text("GPS", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TripStatusChip(status: String) {
    val (bgColor, textColor) = when (status) {
        "EN PROGRESO" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "FINALIZADO" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        else -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bgColor) {
        Text(
            text = status,
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
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
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