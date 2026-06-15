package com.cargoexpress.app.core.presentation.vehicle.editVehicle

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.presentation.common.ConfirmationModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehicleScreen(
    vehicleId: Int,
    viewModel: EditVehicleViewModel,
    navController: NavController
) {
    val vehicleState by viewModel.vehicleState

    var name by remember { mutableStateOf("") }
    var nameTouched by remember { mutableStateOf(false) }
    var isNameLoading by remember { mutableStateOf(false) }

    var showModal by remember { mutableStateOf(false) }
    var modalSuccess by remember { mutableStateOf(false) }
    var modalMessage by remember { mutableStateOf("") }

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicle(vehicleId)
    }

    LaunchedEffect(vehicleState.data) {
        vehicleState.data?.let { vehicle ->
            if (!nameTouched) name = vehicle.name
        }
    }

    val isNameValid = name.isNotBlank() && name.length <= 60
    val showNameError = nameTouched && !isNameValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "EDITAR VEHICULO",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Poner a punto la máquina",
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
        }
    ) { paddingValues ->
        when {
            vehicleState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }

            vehicleState.message.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = vehicleState.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // — Sección: actualizar nombre —
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Actualizar Nombre",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            HorizontalDivider()

                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    if (it.length <= 60) {
                                        name = it
                                        nameTouched = true
                                    }
                                },
                                label = { Text("Nombre del vehículo") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Badge, contentDescription = "Nombre")
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = showNameError,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (showNameError) {
                                    Text(
                                        text = "El nombre es obligatorio (máx. 60 caracteres)",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                Text(
                                    text = "${name.length}/60",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    if (isNameValid) {
                                        isNameLoading = true
                                        viewModel.updateVehicleName(vehicleId, name) { result ->
                                            isNameLoading = false
                                            when (result) {
                                                is Resource.Success -> {
                                                    modalSuccess = true
                                                    modalMessage = "Nombre actualizado correctamente"
                                                    nameTouched = false
                                                    viewModel.loadVehicle(vehicleId)
                                                }
                                                is Resource.Error -> {
                                                    modalSuccess = false
                                                    modalMessage = result.message ?: "No se pudo actualizar el nombre"
                                                }
                                            }
                                            showModal = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                                enabled = isNameValid && !isNameLoading
                            ) {
                                if (isNameLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        "Actualizar Nombre",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    if (showModal) {
        ConfirmationModal(
            isSuccess = modalSuccess,
            message = modalMessage,
            onConfirm = { showModal = false },
            onDismiss = { showModal = false }
        )
    }
}
