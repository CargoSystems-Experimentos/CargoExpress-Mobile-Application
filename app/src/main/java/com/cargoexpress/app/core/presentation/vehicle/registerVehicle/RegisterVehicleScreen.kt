package com.cargoexpress.app.core.presentation.vehicle.registerVehicle

import androidx.compose.foundation.layout.*
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
import com.cargoexpress.app.core.domain.Vehicle
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.presentation.common.ConfirmationModal
import androidx.navigation.NavController

@Composable
fun RegisterVehicleScreen(
    navController: NavController,
    viewModel: RegisterVehicleViewModel = viewModel(),
    onVehicleRegistered: (Vehicle) -> Unit
) {
    var model by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var rawTractorPlate by remember { mutableStateOf("") }
    var maxLoad by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }

    var modelTouched by remember { mutableStateOf(false) }
    var plateTouched by remember { mutableStateOf(false) }
    var tractorTouched by remember { mutableStateOf(false) }
    var maxLoadTouched by remember { mutableStateOf(false) }
    var volumeTouched by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }

    // Validaciones
    val isModelValid = model.isNotBlank()
    val showModelError = modelTouched && !isModelValid

    val isPlateValid = plate.matches(Regex("[A-Z0-9]{3}-[A-Z0-9]{3}")) && plate.firstOrNull()?.isLetter() == true
    val showPlateError = plateTouched && !isPlateValid

    val tractorPlateFormatted = "X" + rawTractorPlate
    val isTractorPlateValid = tractorPlateFormatted.matches(Regex("X[A-Z0-9]{2}-[A-Z0-9]{3}"))
    val showTractorPlateError = tractorTouched && !isTractorPlateValid

    val isMaxLoadValid = maxLoad.isNotBlank() && maxLoad.toFloatOrNull() != null && maxLoad.toFloatOrNull()!! > 0
    val showMaxLoadError = maxLoadTouched && !isMaxLoadValid

    val isVolumeValid = volume.isNotBlank() && volume.toFloatOrNull() != null && volume.toFloatOrNull()!! > 0
    val showVolumeError = volumeTouched && !isVolumeValid

    val isFormValid = isModelValid && isPlateValid && isTractorPlateValid && isMaxLoadValid && isVolumeValid

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Título
            Text(
                text = "Registrar Vehículo",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Modelo
            OutlinedTextField(
                value = model,
                onValueChange = {
                    model = it
                    modelTouched = true
                },
                label = { Text("Modelo") },
                leadingIcon = { Icon(imageVector = Icons.Filled.DirectionsCar, contentDescription = "Modelo") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                isError = showModelError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showModelError) {
                Text(
                    text = "El modelo es obligatorio",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Placa (XXX-XXX)
            OutlinedTextField(
                value = plate,
                onValueChange = {
                    val input = it.uppercase().filter { char -> char.isLetterOrDigit() || char == '-' }
                    plate = if (input.length <= 7) input else input.take(7)
                    plateTouched = true
                },
                label = { Text("Placa (ej: A1B-000)") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Info, contentDescription = "Placa") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                isError = showPlateError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showPlateError) {
                Text(
                    text = "La placa es inválida. Debe empezar con una letra y seguir el formato XXX-XXX",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Placa del Tractor (X00-000)
            OutlinedTextField(
                value = tractorPlateFormatted,
                onValueChange = {
                    val withoutX = it.removePrefix("X")
                    rawTractorPlate = withoutX.filter { char -> char.isLetterOrDigit() || char == '-' }.take(6)
                    tractorTouched = true
                },
                label = { Text("Placa Tractor (ej: X11-111)") },
                leadingIcon = { Icon(imageVector = Icons.Filled.LocalShipping, contentDescription = "Placa Tractor") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                isError = showTractorPlateError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showTractorPlateError) {
                Text(
                    text = "La placa del tractor es inválida. Debe empezar con X y seguir el formato X00-000",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Carga Máxima (kg)
            OutlinedTextField(
                value = maxLoad,
                onValueChange = {
                    maxLoad = it.filter { char -> char.isDigit() || char == '.' }
                    maxLoadTouched = true
                },
                label = { Text("Carga Máxima (kg)") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Scale, contentDescription = "Carga Máxima") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                isError = showMaxLoadError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showMaxLoadError) {
                Text(
                    text = "La carga máxima es obligatoria y debe ser mayor a 0",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Volumen (m³)
            OutlinedTextField(
                value = volume,
                onValueChange = {
                    volume = it.filter { char -> char.isDigit() || char == '.' }
                    volumeTouched = true
                },
                label = { Text("Volumen (m³)") },
                leadingIcon = { Icon(imageVector = Icons.Filled.ViewWeek, contentDescription = "Volumen") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                isError = showVolumeError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showVolumeError) {
                Text(
                    text = "El volumen es obligatorio y debe ser mayor a 0",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFFFFEB3B)
                )
            }

            Button(
                onClick = {
                    if (isFormValid) {
                        isLoading = true
                        viewModel.model = model
                        viewModel.plate = plate
                        viewModel.tractorPlate = tractorPlateFormatted
                        viewModel.maxLoad = maxLoad.toFloatOrNull() ?: 0f
                        viewModel.volume = volume.toFloatOrNull() ?: 0f

                        viewModel.registerVehicle { result ->
                            isLoading = false
                            if (result is Resource.Success && result.data != null) {
                                onVehicleRegistered(result.data)
                                confirmModalSuccess = true
                                confirmModalMessage = "Vehículo registrado correctamente"
                            } else {
                                confirmModalSuccess = false
                                confirmModalMessage = "No se pudo registrar el vehículo"
                            }
                            showConfirmModal = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = isFormValid && !isLoading
            ) {
                Text(
                    "Registrar Vehículo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showConfirmModal) {
        ConfirmationModal(
            isSuccess = confirmModalSuccess,
            message = confirmModalMessage,
            onConfirm = {
                showConfirmModal = false
                if (confirmModalSuccess) {
                    model = ""
                    plate = ""
                    rawTractorPlate = ""
                    maxLoad = ""
                    volume = ""
                    modelTouched = false
                    plateTouched = false
                    tractorTouched = false
                    maxLoadTouched = false
                    volumeTouched = false
                    navController.navigate("vehicles")
                }
            },
            onDismiss = { showConfirmModal = false }
        )
    }
}