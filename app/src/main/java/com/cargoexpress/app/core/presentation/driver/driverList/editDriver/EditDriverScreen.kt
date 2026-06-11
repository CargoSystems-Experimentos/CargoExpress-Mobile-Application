package com.cargoexpress.app.core.presentation.driver.driverList.editDriver

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
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.presentation.common.ConfirmationModal

private val driverStates = listOf("AVAILABLE", "UNAVAILABLE", "INACTIVE")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDriverScreen(
    driverId: Int,
    viewModel: EditDriverViewModel,
    navController: NavController
) {
    val driverState by viewModel.driverState

    var name by remember { mutableStateOf("") }
    var nameTouched by remember { mutableStateOf(false) }
    var contactNumber by remember { mutableStateOf("") }
    var contactTouched by remember { mutableStateOf(false) }
    var isInfoLoading by remember { mutableStateOf(false) }

    var selectedState by remember { mutableStateOf("") }
    var stateExpanded by remember { mutableStateOf(false) }
    var isStateLoading by remember { mutableStateOf(false) }

    var showModal by remember { mutableStateOf(false) }
    var modalSuccess by remember { mutableStateOf(false) }
    var modalMessage by remember { mutableStateOf("") }

    LaunchedEffect(driverId) {
        viewModel.loadDriver(driverId)
    }

    LaunchedEffect(driverState.data) {
        driverState.data?.let { driver ->
            if (!nameTouched) name = driver.name
            if (!contactTouched) contactNumber = driver.contactNumber
            if (selectedState.isEmpty()) selectedState = driver.state
        }
    }

    val isNameValid = name.isNotBlank() && name.length <= 60
    val showNameError = nameTouched && !isNameValid

    val isContactValid = contactNumber.length == 9 && contactNumber.all { it.isDigit() }
    val showContactError = contactTouched && !isContactValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Conductor",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
            driverState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }

            driverState.message.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = driverState.message,
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
                    // — Sección: actualizar información —
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Actualizar Información",
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
                                label = { Text("Nombre del conductor") },
                                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Nombre") },
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

                            OutlinedTextField(
                                value = contactNumber,
                                onValueChange = {
                                    val digits = it.filter { ch -> ch.isDigit() }.take(9)
                                    contactNumber = digits
                                    contactTouched = true
                                },
                                label = { Text("Número de contacto") },
                                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = "Contacto") },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = showContactError,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (showContactError) {
                                Text(
                                    text = "El número de contacto debe tener exactamente 9 dígitos",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Button(
                                onClick = {
                                    nameTouched = true
                                    contactTouched = true
                                    if (isNameValid && isContactValid) {
                                        isInfoLoading = true
                                        viewModel.updateDriver(driverId, name, contactNumber) { result ->
                                            isInfoLoading = false
                                            when (result) {
                                                is Resource.Success -> {
                                                    modalSuccess = true
                                                    modalMessage = "Conductor actualizado correctamente"
                                                    nameTouched = false
                                                    contactTouched = false
                                                    viewModel.loadDriver(driverId)
                                                }
                                                is Resource.Error -> {
                                                    modalSuccess = false
                                                    modalMessage = result.message ?: "No se pudo actualizar el conductor"
                                                }
                                            }
                                            showModal = true
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                                enabled = isNameValid && isContactValid && !isInfoLoading
                            ) {
                                if (isInfoLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Actualizar Conductor", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // — Sección: actualizar estado —
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Actualizar Estado",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            HorizontalDivider()

                            ExposedDropdownMenuBox(
                                expanded = stateExpanded,
                                onExpandedChange = { stateExpanded = !stateExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedState,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Estado") },
                                    leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = "Estado") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = stateExpanded,
                                    onDismissRequest = { stateExpanded = false }
                                ) {
                                    driverStates.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                selectedState = option
                                                stateExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (selectedState.isNotBlank()) {
                                        isStateLoading = true
                                        viewModel.updateDriverState(driverId, selectedState) { result ->
                                            isStateLoading = false
                                            when (result) {
                                                is Resource.Success -> {
                                                    modalSuccess = true
                                                    modalMessage = "Estado actualizado correctamente"
                                                    viewModel.loadDriver(driverId)
                                                }
                                                is Resource.Error -> {
                                                    modalSuccess = false
                                                    modalMessage = result.message ?: "No se pudo actualizar el estado"
                                                }
                                            }
                                            showModal = true
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                                enabled = selectedState.isNotBlank() && !isStateLoading
                            ) {
                                if (isStateLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Actualizar Estado", color = Color.Black, fontWeight = FontWeight.Bold)
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
