package com.cargoexpress.app.core.presentation.vehicle


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.cargoexpress.app.core.domain.Vehicle
import com.cargoexpress.app.core.common.Constants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(viewModel: VehicleListViewModel = viewModel(), navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var filterByModel by remember { mutableStateOf(true) }
    var sortAscending by remember { mutableStateOf(true) }
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.getVehiclesForEntrepreneur(entrepreneurId = Constants.ENTREPRENEUR_ID, token = Constants.TOKEN)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Título
            Text(
                text = "Mis Vehículos",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campo de búsqueda mejorado
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar vehículo") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Filtros y Ordenamiento
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filtro por tipo
                FilterChip(
                    selected = filterByModel,
                    onClick = { filterByModel = true },
                    label = { Text("Modelo") },
                    leadingIcon = { Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                FilterChip(
                    selected = !filterByModel,
                    onClick = { filterByModel = false },
                    label = { Text("Placa") },
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Botón de ordenamiento
                FilterChip(
                    selected = true,
                    onClick = { sortAscending = !sortAscending },
                    label = {
                        Text(if (sortAscending) "↑ A-Z" else "↓ Z-A")
                    },
                    leadingIcon = { Icon(if (sortAscending) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Lista de vehículos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredVehicles = state.data?.filter { vehicle ->
                    if (filterByModel) {
                        vehicle.model.contains(searchQuery, ignoreCase = true)
                    } else {
                        vehicle.plate.contains(searchQuery, ignoreCase = true)
                    }
                } ?: emptyList()

                val sortedVehicles = if (filterByModel) {
                    if (sortAscending) {
                        filteredVehicles.sortedBy { it.model }
                    } else {
                        filteredVehicles.sortedByDescending { it.model }
                    }
                } else {
                    filteredVehicles
                }

                if (sortedVehicles.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron vehículos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    items(sortedVehicles.size) { index ->
                        val vehicle = sortedVehicles[index]
                        VehicleItem(vehicle = vehicle)
                    }
                }
            }
        }

        // Botón flotante para agregar vehículo
        FloatingActionButton(
            onClick = { navController.navigate("register_vehicle?token=${Constants.TOKEN}") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFFFEB3B)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar vehículo", tint = Color.Black)
        }
    }
}

@Composable
fun VehicleItem(vehicle: Vehicle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Modelo con icono
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint = Color(0xFFFFEB3B),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = vehicle.model,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Separador
            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Placa con icono
            VehicleInfoItem(
                icon = Icons.Filled.Info,
                label = "Placa",
                value = vehicle.plate
            )

            // Carga máxima con icono
            VehicleInfoItem(
                icon = Icons.Filled.Scale,
                label = "Carga",
                value = "${vehicle.maxLoad} kg"
            )

            // Volumen con icono
            VehicleInfoItem(
                icon = Icons.Filled.ViewWeek,
                label = "Volumen",
                value = "${vehicle.volume} m³"
            )
        }
    }
}

@Composable
fun VehicleInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(70.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}