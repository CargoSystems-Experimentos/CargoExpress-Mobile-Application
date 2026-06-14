package com.cargoexpress.app.core.presentation.vehicle.registerVehicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cargoexpress.app.core.domain.Vehicle
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.presentation.common.ConfirmationModal
import androidx.navigation.NavController
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterVehicleScreen(
    navController: NavController,
    viewModel: RegisterVehicleViewModel = viewModel(),
    onVehicleRegistered: (Vehicle) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var rawTractorPlate by remember { mutableStateOf("") }
    var maxLoad by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }

    var nameTouched by remember { mutableStateOf(false) }
    var modelTouched by remember { mutableStateOf(false) }
    var plateTouched by remember { mutableStateOf(false) }
    var tractorTouched by remember { mutableStateOf(false) }
    var maxLoadTouched by remember { mutableStateOf(false) }
    var volumeTouched by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }

    val isNameValid = name.isNotBlank() && name.length <= 60
    val showNameError = nameTouched && !isNameValid

    val isModelValid = model.isNotBlank()
    val showModelError = modelTouched && !isModelValid

    val isPlateValid = plate.matches(Regex("[A-Z0-9]{3}-[A-Z0-9]{3}")) && plate.firstOrNull()?.isLetter() == true
    val showPlateError = plateTouched && !isPlateValid

    val tractorPlateFormatted = "X" + rawTractorPlate
    val isTractorPlateValid = tractorPlateFormatted.matches(Regex("X[A-Z0-9]{2}-[A-Z0-9]{3}"))
    val showTractorPlateError = tractorTouched && !isTractorPlateValid

    val maxLoadValue = maxLoad.toDoubleOrNull()
    val isMaxLoadValid = maxLoad.isNotBlank() && maxLoadValue != null && maxLoadValue > 0
    val showMaxLoadError = maxLoadTouched && !isMaxLoadValid

    val volumeValue = volume.toDoubleOrNull()
    val isVolumeValid = volume.isNotBlank() && volumeValue != null && volumeValue > 0
    val showVolumeError = volumeTouched && !isVolumeValid

    val isFormValid = isNameValid && isModelValid && isPlateValid && isTractorPlateValid && isMaxLoadValid && isVolumeValid
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "NUEVO VEHICULO",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Una nueva bestia en el garaje",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal, fontSize = 15.sp),
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retroceder")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (isFormValid) {
                                isLoading = true
                                viewModel.name = name
                                viewModel.model = model
                                viewModel.plate = plate
                                viewModel.tractorPlate = tractorPlateFormatted
                                viewModel.maxLoad = maxLoadValue ?: 0.0
                                viewModel.volume = volumeValue ?: 0.0

                                viewModel.registerVehicle { result ->
                                    isLoading = false
                                    if (result is Resource.Success && result.data != null) {
                                        onVehicleRegistered(result.data)
                                        confirmModalSuccess = true
                                        confirmModalMessage = "Vehículo registrado correctamente"
                                    } else {
                                        confirmModalSuccess = false
                                        confirmModalMessage = (result as? Resource.Error)?.message ?: "No se pudo registrar el vehículo"
                                    }
                                    showConfirmModal = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
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
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Nombre
            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 60) {
                        name = it
                        nameTouched = true
                    }
                },
                label = { Text("Nombre del vehículo") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Badge, contentDescription = "Nombre") },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                maxLines = 1,
                isError = showNameError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showNameError) {
                    Text(
                        text = "El nombre es obligatorio (máx. 60 caracteres)",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        textAlign = TextAlign.Start
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                Text(
                    text = "${name.length}/60",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Modelo
            OutlinedTextField(
                value = model,
                onValueChange = {
                    if (it.length <= 60) {
                        model = it
                        modelTouched = true
                    }
                },
                label = { Text("Modelo") },
                leadingIcon = { Icon(imageVector = Icons.Filled.DirectionsCar, contentDescription = "Modelo") },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                maxLines = 1,
                isError = showModelError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showModelError) {
                    Text(
                        text = "El modelo es obligatorio",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        textAlign = TextAlign.Start
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                Text(
                    text = "${model.length}/60",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

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
                shape = RoundedCornerShape(8.dp),
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
                    textAlign = TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
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
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                maxLines = 1,
                isError = showTractorPlateError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showTractorPlateError) {
                Text(
                    text = "La placa del tractor es inválida. Debe seguir el formato X00-000",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Carga Máxima (kg)
            OutlinedTextField(
                value = maxLoad,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() || it == '.' }
                    val dotIdx = filtered.indexOf('.')
                    val valid = if (dotIdx == -1) {
                        filtered.length <= 8
                    } else {
                        filtered.indexOf('.', dotIdx + 1) == -1 &&
                            filtered.length - dotIdx - 1 <= 2 &&
                            dotIdx <= 8
                    }
                    if (filtered.isEmpty() || valid) {
                        maxLoad = filtered
                        maxLoadTouched = true
                    }
                },
                label = { Text("Carga Máxima (kg)") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Scale, contentDescription = "Carga Máxima") },
                shape = RoundedCornerShape(8.dp),
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
                    textAlign = TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Volumen (m³)
            OutlinedTextField(
                value = volume,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() || it == '.' }
                    val dotIdx = filtered.indexOf('.')
                    val valid = if (dotIdx == -1) {
                        filtered.length <= 8
                    } else {
                        filtered.indexOf('.', dotIdx + 1) == -1 &&
                            filtered.length - dotIdx - 1 <= 2 &&
                            dotIdx <= 8
                    }
                    if (filtered.isEmpty() || valid) {
                        volume = filtered
                        volumeTouched = true
                    }
                },
                label = { Text("Volumen (m³)") },
                leadingIcon = { Icon(imageVector = Icons.Filled.ViewWeek, contentDescription = "Volumen") },
                shape = RoundedCornerShape(8.dp),
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
                    textAlign = TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFFFFEB3B)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showConfirmModal) {
        ConfirmationModal(
            isSuccess = confirmModalSuccess,
            message = confirmModalMessage,
            onConfirm = {
                showConfirmModal = false
                if (confirmModalSuccess) {
                    name = ""
                    model = ""
                    plate = ""
                    rawTractorPlate = ""
                    maxLoad = ""
                    volume = ""
                    nameTouched = false
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
