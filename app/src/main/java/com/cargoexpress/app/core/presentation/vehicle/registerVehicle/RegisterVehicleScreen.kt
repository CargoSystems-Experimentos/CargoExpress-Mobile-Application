package com.cargoexpress.app.core.presentation.vehicle.registerVehicle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cargoexpress.app.core.domain.Vehicle
import com.cargoexpress.app.core.common.Resource
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun RegisterVehicleScreen(
    navController: NavController,
    viewModel: RegisterVehicleViewModel = viewModel(),
    onVehicleRegistered: (Vehicle) -> Unit
) {
    var model by remember { mutableStateOf(viewModel.model) }
    var plate by remember { mutableStateOf(viewModel.plate) }
    var tractorPlate by remember { mutableStateOf(viewModel.tractorPlate) }
    var maxLoad by remember { mutableStateOf(viewModel.maxLoad.toString()) }
    var volume by remember { mutableStateOf(viewModel.volume.toString()) }

    var modelError by remember { mutableStateOf<String?>(null) }
    var plateError by remember { mutableStateOf<String?>(null) }
    var tractorPlateError by remember { mutableStateOf<String?>(null) }
    var maxLoadError by remember { mutableStateOf<String?>(null) }
    var volumeError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Registrar Nuevo Vehículo",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                value = model,
                label = "Modelo",
                onValueChange = {
                    model = it
                    modelError = if (it.isBlank()) "El modelo es obligatorio" else null
                },
                error = modelError
            )
            InputField(
                value = plate,
                label = "Placa",
                onValueChange = {
                    plate = it
                    plateError = if (it.isBlank()) "La placa es obligatoria" else null
                },
                error = plateError
            )
            InputField(
                value = tractorPlate,
                label = "Placa del Tractor",
                onValueChange = {
                    tractorPlate = it
                    tractorPlateError = if (it.isBlank()) "La placa del tractor es obligatoria" else null
                },
                error = tractorPlateError
            )
            InputField(
                value = maxLoad,
                label = "Carga Máxima",
                onValueChange = {
                    maxLoad = it
                    maxLoadError = if (it.isBlank() || it.toFloatOrNull() == null) "La carga máxima es obligatoria y debe ser un número" else null
                },
                error = maxLoadError
            )
            InputField(
                value = volume,
                label = "Volumen",
                onValueChange = {
                    volume = it
                    volumeError = if (it.isBlank() || it.toFloatOrNull() == null) "El volumen es obligatorio y debe ser un número" else null
                },
                error = volumeError
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFFEB3B))
            }

            Button(
                onClick = {
                    modelError = if (model.isBlank()) "El modelo es obligatorio" else null
                    plateError = if (plate.isBlank()) "La placa es obligatoria" else null
                    tractorPlateError = if (tractorPlate.isBlank()) "La placa del tractor es obligatoria" else null
                    maxLoadError = if (maxLoad.isBlank() || maxLoad.toFloatOrNull() == null) "La carga máxima es obligatoria y debe ser un número" else null
                    volumeError = if (volume.isBlank() || volume.toFloatOrNull() == null) "El volumen es obligatorio y debe ser un número" else null

                    val valid = listOf(modelError, plateError, tractorPlateError, maxLoadError, volumeError).all { it == null }

                    if (valid) {
                        isLoading = true
                        viewModel.model = model
                        viewModel.plate = plate
                        viewModel.tractorPlate = tractorPlate
                        viewModel.maxLoad = maxLoad.toFloat()
                        viewModel.volume = volume.toFloat()

                        viewModel.registerVehicle { result ->
                            isLoading = false
                            val message = if (result is Resource.Success && result.data != null) {
                                onVehicleRegistered(result.data)

                                model = ""
                                plate = ""
                                tractorPlate = ""
                                maxLoad = ""
                                volume = ""
                                navController.navigate("vehicles") // Navigate to VehicleListScreen
                                "Vehiculo registrado correctamente"
                            } else {
                                "No se pudo registrar el vehiculo"
                            }

                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor, completa todos los campos correctamente")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = !isLoading
            ) {
                Text("Register Vehicle", color = Color.Black)
            }
        }
    }
}

@Composable
fun InputField(value: String, label: String, onValueChange: (String) -> Unit, error: String?) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}