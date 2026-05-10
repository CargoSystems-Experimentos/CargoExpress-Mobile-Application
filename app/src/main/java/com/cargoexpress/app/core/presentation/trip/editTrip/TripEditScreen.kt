package com.cargoexpress.app.core.presentation.trip.editTrip

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.repository.TripRepository
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Resource

@Composable
fun TripEditScreen(
    tripId: Int,
    tripRepository: TripRepository,
    navController: NavController
) {
    val factory = remember { TripEditViewModelFactory(tripRepository) }
    val viewModel: TripEditViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf(0) }
    var loadLocation by remember { mutableStateOf("") }
    var loadDate by remember { mutableStateOf("") }
    var unloadLocation by remember { mutableStateOf("") }
    var unloadDate by remember { mutableStateOf("") }
    var driverId by remember { mutableStateOf(0) }
    var vehicleId by remember { mutableStateOf(0) }
    var clientId by remember { mutableStateOf(0) }
    var evidenceImg by remember { mutableStateOf("") }

    LaunchedEffect(uiState.trip) {
        uiState.trip?.let { trip ->
            name = trip.name
            type = trip.type
            weight = trip.weight
            loadLocation = trip.loadLocation
            loadDate = trip.loadDate
            unloadLocation = trip.unloadLocation
            unloadDate = trip.unloadDate
            driverId = trip.driverId
            vehicleId = trip.vehicleId
            clientId = trip.clientId
            evidenceImg = trip.evidenceImg
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Trip Name") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Cargo Type") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = weight.toString(),
            onValueChange = { weight = it.toIntOrNull() ?: 0 },
            label = { Text("Weight") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = loadLocation,
            onValueChange = { loadLocation = it },
            label = { Text("Load Location") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = loadDate,
            onValueChange = { loadDate = it },
            label = { Text("Load Date") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = unloadLocation,
            onValueChange = { unloadLocation = it },
            label = { Text("Unload Location") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = unloadDate,
            onValueChange = { unloadDate = it },
            label = { Text("Unload Date") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = driverId.toString(),
            onValueChange = { driverId = it.toIntOrNull() ?: 0 },
            label = { Text("Driver ID") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = vehicleId.toString(),
            onValueChange = { vehicleId = it.toIntOrNull() ?: 0 },
            label = { Text("Vehicle ID") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = clientId.toString(),
            onValueChange = { clientId = it.toIntOrNull() ?: 0 },
            label = { Text("Client ID") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = evidenceImg,
            onValueChange = { evidenceImg = it },
            label = { Text("Evidence Image URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        val trip = uiState.trip
        Button(
            onClick = {
                scope.launch {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        loading = true
                        viewModel.updateTrip { result ->
                            loading = false
                            if (result is Resource.Success) {
                                navController.popBackStack()
                            } else {
                                // Handle error
                            }
                        }
                    }
                }
            },
            enabled = !loading && trip != null && (
                    name != trip.name ||
                            type != trip.type ||
                            weight != trip.weight ||
                            loadLocation != trip.loadLocation ||
                            loadDate != trip.loadDate ||
                            unloadLocation != trip.unloadLocation ||
                            unloadDate != trip.unloadDate ||
                            driverId != trip.driverId ||
                            vehicleId != trip.vehicleId ||
                            clientId != trip.clientId ||
                            evidenceImg != trip.evidenceImg
                    )
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                Text("Guardar Cambios")
            }
        }
    }
}