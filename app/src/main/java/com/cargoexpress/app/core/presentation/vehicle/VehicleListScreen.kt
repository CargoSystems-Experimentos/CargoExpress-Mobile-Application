package com.cargoexpress.app.core.presentation.vehicle


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.getVehiclesForEntrepreneur(entrepreneurId = Constants.ENTREPRENEUR_ID, token = Constants.TOKEN)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            // Campo de búsqueda
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Buscar vehículo") },
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFF1F5F9)),
                singleLine = true
            )

            // Lista de vehículos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                val filteredVehicles = state.data?.filter { vehicle ->
                    vehicle.model.contains(searchQuery, ignoreCase = true) ||
                            vehicle.plate.contains(searchQuery, ignoreCase = true) ||
                            vehicle.tractorPlate.contains(searchQuery, ignoreCase = true)
                } ?: emptyList()

                if (filteredVehicles.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron vehículos.",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    items(filteredVehicles.size) { index ->
                        val vehicle = filteredVehicles[index]
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
            containerColor = Color(0xFFF1F504)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun VehicleItem(vehicle: Vehicle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF),
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modelo: ${vehicle.model}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Placa: ${vehicle.plate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "Carga máxima: ${vehicle.maxLoad} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "Volumen: ${vehicle.volume} m³",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}