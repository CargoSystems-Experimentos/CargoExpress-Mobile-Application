package com.cargoexpress.app.core.presentation.trip.registerTrip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.domain.Driver
import com.cargoexpress.app.core.domain.Trip
import com.cargoexpress.app.core.domain.Vehicle
import com.cargoexpress.app.core.presentation.common.ConfirmationModal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTripScreen(
    viewModel: RegisterTripViewModel = viewModel(),
    navController: NavController,
    onTripRegistered: (Trip) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var loadLocation by remember { mutableStateOf("") }
    var unloadLocation by remember { mutableStateOf("") }

    var loadCalendar by remember { mutableStateOf<Calendar?>(null) }
    var unloadCalendar by remember { mutableStateOf<Calendar?>(null) }

    var driverId by remember { mutableStateOf(0) }
    var driverName by remember { mutableStateOf("") }
    var vehicleId by remember { mutableStateOf(0) }
    var vehicleName by remember { mutableStateOf("") }
    var clientDni by remember { mutableStateOf("") }
    var resolvedClientId by remember { mutableStateOf(0) }
    var clientFoundName by remember { mutableStateOf("") }
    var clientDniError by remember { mutableStateOf("") }
    var isDniValidating by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }
    var registeredTrip by remember { mutableStateOf<Trip?>(null) }

    var nameTouched by remember { mutableStateOf(false) }
    var typeTouched by remember { mutableStateOf(false) }
    var weightTouched by remember { mutableStateOf(false) }
    var loadLocationTouched by remember { mutableStateOf(false) }
    var unloadLocationTouched by remember { mutableStateOf(false) }

    var showDriverModal by remember { mutableStateOf(false) }
    var showVehicleModal by remember { mutableStateOf(false) }

    val dateTimeFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val loadDateText = loadCalendar?.time?.let { dateTimeFormat.format(it) } ?: ""
    val unloadDateText = unloadCalendar?.time?.let { dateTimeFormat.format(it) } ?: ""

    val isNameValid = name.isNotBlank()
    val showNameError = nameTouched && !isNameValid

    val isTypeValid = type.isNotBlank()
    val showTypeError = typeTouched && !isTypeValid

    val isWeightValid = weight.toFloatOrNull()?.let { it > 0f } == true
    val showWeightError = weightTouched && !isWeightValid

    val isLoadLocationValid = loadLocation.isNotBlank()
    val showLoadLocationError = loadLocationTouched && !isLoadLocationValid

    val isUnloadLocationValid = unloadLocation.isNotBlank()
    val showUnloadLocationError = unloadLocationTouched && !isUnloadLocationValid

    val isDriverValid = driverId > 0
    val isVehicleValid = vehicleId > 0
    val isClientValid = resolvedClientId > 0

    val isLoadValid = loadCalendar != null
    val isUnloadValid = unloadCalendar != null

    val isFormValid =
        isNameValid &&
                isTypeValid &&
                isWeightValid &&
                isLoadLocationValid &&
                isUnloadLocationValid &&
                isDriverValid &&
                isVehicleValid &&
                isLoadValid &&
                isUnloadValid &&
                isClientValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Viaje", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retroceder")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (isFormValid) {
                        isLoading = true
                        viewModel.name = name
                        viewModel.type = type
                        viewModel.weight = weight.toIntOrNull() ?: 0
                        viewModel.loadLocation = loadLocation
                        viewModel.loadDate = toBackendDateTime(loadCalendar)
                        viewModel.unloadLocation = unloadLocation
                        viewModel.unloadDate = toBackendDateTime(unloadCalendar)
                        viewModel.driverId = driverId
                        viewModel.vehicleId = vehicleId
                        viewModel.clientId = resolvedClientId
                        viewModel.evidenceImg = ""

                        viewModel.registerTrip { result ->
                            isLoading = false
                            if (result is Resource.Success && result.data != null) {
                                registeredTrip = result.data
                                onTripRegistered(result.data)
                                confirmModalSuccess = true
                                confirmModalMessage = "Viaje registrado correctamente"
                            } else {
                                confirmModalSuccess = false
                                confirmModalMessage = "No se pudo registrar el viaje"
                            }
                            showConfirmModal = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = isFormValid && !isLoading
            ) {
                Text(
                    "Registrar Viaje",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 60) {
                            name = it
                            nameTouched = true
                        }
                    },
                    label = { Text("Nombre del Viaje") },
                    leadingIcon = { Icon(Icons.Filled.LocalShipping, contentDescription = "Nombre") },
                    shape = RoundedCornerShape(16.dp),
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
                            text = "El nombre del viaje es obligatorio",
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
            }

            item {
                val cargoTypes = listOf("ESTÁNDAR", "FRÁGIL", "PESADO", "VALIOSO", "URGENTE", "PERECIBLE")
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Carga") },
                        leadingIcon = { Icon(Icons.Filled.Category, contentDescription = "Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        isError = showTypeError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        cargoTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    typeTouched = true
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
                if (showTypeError) {
                    Text(
                        text = "El tipo de carga es obligatorio",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        val filtered = it.filter { char -> char.isDigit() || char == '.' }
                        val parsed = filtered.toFloatOrNull()
                        if (filtered.isEmpty() || filtered.endsWith('.') || (parsed != null && parsed <= 50000f)) {
                            weight = filtered
                            weightTouched = true
                        }
                    },
                    label = { Text("Peso (kg)") },
                    leadingIcon = { Icon(Icons.Filled.Scale, contentDescription = "Peso") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    isError = showWeightError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                if (showWeightError) {
                    Text(
                        text = "El peso es obligatorio y debe ser mayor a 0",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                OutlinedTextField(
                    value = loadLocation,
                    onValueChange = {
                        if (it.length <= 100) {
                            loadLocation = it
                            loadLocationTouched = true
                        }
                    },
                    label = { Text("Ubicación de Carga") },
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = "Ubicación") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    isError = showLoadLocationError,
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
                    if (showLoadLocationError) {
                        Text(
                            text = "La ubicación de carga es obligatoria",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = "${loadLocation.length}/100",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                Text(
                    text = "Fecha y hora de Carga",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        showDateTimePicker(
                            context = context,
                            initial = loadCalendar
                        ) { selected ->
                            loadCalendar = selected
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (loadDateText.isBlank()) "Seleccionar carga" else loadDateText,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (!isLoadValid) {
                    Text(
                        text = "Debes seleccionar la fecha y hora de carga",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                OutlinedTextField(
                    value = unloadLocation,
                    onValueChange = {
                        if (it.length <= 100) {
                            unloadLocation = it
                            unloadLocationTouched = true
                        }
                    },
                    label = { Text("Ubicación de Descarga") },
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = "Ubicación") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    isError = showUnloadLocationError,
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
                    if (showUnloadLocationError) {
                        Text(
                            text = "La ubicación de descarga es obligatoria",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = "${unloadLocation.length}/100",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                Text(
                    text = "Fecha y hora de Descarga",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        showDateTimePicker(
                            context = context,
                            initial = unloadCalendar
                        ) { selected ->
                            unloadCalendar = selected
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (unloadDateText.isBlank()) "Seleccionar descarga" else unloadDateText,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (!isUnloadValid) {
                    Text(
                        text = "Debes seleccionar la fecha y hora de descarga",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Button(
                    onClick = { showDriverModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (driverName.isBlank()) "Seleccionar Conductor" else driverName,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (!isDriverValid) {
                    Text(
                        text = "Debes seleccionar un conductor",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                }
            }

            item {
                Button(
                    onClick = { showVehicleModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (vehicleName.isBlank()) "Seleccionar Vehículo" else vehicleName,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (!isVehicleValid) {
                    Text(
                        text = "Debes seleccionar un vehículo",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = clientDni,
                    onValueChange = {
                        clientDni = it.filter { c -> c.isDigit() }.take(8)
                        resolvedClientId = 0
                        clientFoundName = ""
                        clientDniError = ""
                    },
                    label = { Text("DNI del Cliente") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "DNI") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (clientDni.isNotBlank()) {
                                    isDniValidating = true
                                    scope.launch {
                                        val result = viewModel.validateClientDni(clientDni)
                                        isDniValidating = false
                                        result.onSuccess { client ->
                                            resolvedClientId = client.id
                                            clientFoundName = client.name
                                            clientDniError = ""
                                        }.onFailure {
                                            resolvedClientId = 0
                                            clientFoundName = ""
                                            clientDniError = "Cliente no encontrado"
                                        }
                                    }
                                }
                            },
                            enabled = clientDni.isNotBlank() && !isDniValidating
                        ) {
                            if (isDniValidating) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Search, contentDescription = "Verificar DNI")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    isError = clientDniError.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                when {
                    clientFoundName.isNotBlank() -> Text(
                        text = "Cliente: $clientFoundName",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                    clientDniError.isNotBlank() -> Text(
                        text = clientDniError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                    else -> Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFEB3B))
                    }
                }
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
                    name = ""
                    type = ""
                    weight = ""
                    loadLocation = ""
                    unloadLocation = ""
                    loadCalendar = null
                    unloadCalendar = null
                    driverId = 0
                    driverName = ""
                    vehicleId = 0
                    vehicleName = ""
                    clientDni = ""
                    resolvedClientId = 0
                    clientFoundName = ""
                    clientDniError = ""
                    nameTouched = false
                    typeTouched = false
                    weightTouched = false
                    loadLocationTouched = false
                    unloadLocationTouched = false
                    navController.navigate("trips")
                }
            },
            onDismiss = { showConfirmModal = false }
        )
    }

    if (showDriverModal) {
        DriverModal(
            entrepreneurId = Constants.ENTREPRENEUR_ID,
            onDriverSelected = { id, selectedName ->
                driverId = id
                driverName = selectedName
                showDriverModal = false
            },
            onDismiss = { showDriverModal = false }
        )
    }

    if (showVehicleModal) {
        VehicleModal(
            entrepreneurId = Constants.ENTREPRENEUR_ID,
            onVehicleSelected = { id, selectedName ->
                vehicleId = id
                vehicleName = selectedName
                showVehicleModal = false
            },
            onDismiss = { showVehicleModal = false }
        )
    }
}

@Composable
fun DriverModal(
    viewModel: RegisterTripViewModel = viewModel(),
    entrepreneurId: Int,
    onDriverSelected: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var drivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.getDrivers(entrepreneurId).let { result ->
            drivers = when (result) {
                is Resource.Success -> result.data ?: emptyList()
                else -> emptyList()
            }
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Conductor") },
        text = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(drivers.size) { index ->
                        val driver = drivers[index]
                        Text(
                            driver.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDriverSelected(driver.id, driver.name)
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun VehicleModal(
    viewModel: RegisterTripViewModel = viewModel(),
    entrepreneurId: Int,
    onVehicleSelected: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.getVehicles(entrepreneurId).let { result ->
            vehicles = when (result) {
                is Resource.Success -> result.data ?: emptyList()
                else -> emptyList()
            }
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Vehículo") },
        text = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(vehicles.size) { index ->
                        val vehicle = vehicles[index]
                        Text(
                            vehicle.model,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onVehicleSelected(vehicle.id, vehicle.model)
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun showDateTimePicker(
    context: android.content.Context,
    initial: Calendar?,
    onSelected: (Calendar) -> Unit
) {
    val start = initial ?: Calendar.getInstance()

    val dateDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val current = Calendar.getInstance().apply {
                timeInMillis = start.timeInMillis
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    current.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    current.set(Calendar.MINUTE, minute)
                    current.set(Calendar.SECOND, 0)
                    current.set(Calendar.MILLISECOND, 0)
                    onSelected(current)
                },
                current.get(Calendar.HOUR_OF_DAY),
                current.get(Calendar.MINUTE),
                true
            ).show()
        },
        start.get(Calendar.YEAR),
        start.get(Calendar.MONTH),
        start.get(Calendar.DAY_OF_MONTH)
    )

    dateDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
    dateDialog.show()
}

private fun toBackendDateTime(calendar: Calendar?): String {
    if (calendar == null) return ""
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return format.format(calendar.time)
}