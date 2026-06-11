package com.cargoexpress.app.core.presentation.trip.editTrip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Driver
import com.cargoexpress.app.core.domain.Vehicle
import com.cargoexpress.app.core.presentation.common.ConfirmationModal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripEditScreen(
    tripId: Int,
    tripRepository: TripRepository,
    driverRepository: DriverRepository,
    vehicleRepository: VehicleRepository,
    clientRepository: ClientRepository,
    navController: NavController
) {
    val factory = remember {
        TripEditViewModelFactory(tripRepository, driverRepository, vehicleRepository, clientRepository)
    }
    val viewModel: TripEditViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
    var showDriverModal by remember { mutableStateOf(false) }
    var showVehicleModal by remember { mutableStateOf(false) }
    var preloadApplied by remember { mutableStateOf(false) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }

    val dateTimeFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val loadDateText = loadCalendar?.time?.let { dateTimeFormat.format(it) } ?: ""
    val unloadDateText = unloadCalendar?.time?.let { dateTimeFormat.format(it) } ?: ""

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    LaunchedEffect(uiState.trip, uiState.isLoading) {
        if (!uiState.isLoading && uiState.trip != null && !preloadApplied) {
            val trip = uiState.trip!!
            name = trip.name
            type = trip.type
            weight = trip.weight.toString()
            loadLocation = trip.loadLocation
            unloadLocation = trip.unloadLocation
            loadCalendar = parseIsoToCalendar(trip.loadDate)
            unloadCalendar = parseIsoToCalendar(trip.unloadDate)
            driverId = trip.driverId
            vehicleId = trip.vehicleId
            resolvedClientId = trip.clientId
            driverName = uiState.preloadedDriverName
            vehicleName = uiState.preloadedVehicleModel
            clientDni = uiState.preloadedClientDni
            clientFoundName = uiState.preloadedClientName
            preloadApplied = true
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFFFEB3B))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Viaje", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
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
                    isLoading = true
                    viewModel.name = name
                    viewModel.type = type
                    viewModel.weight = weight.toDoubleOrNull() ?: 0.0
                    viewModel.loadLocation = loadLocation
                    viewModel.loadDate = toBackendDateTime(loadCalendar)
                    viewModel.unloadLocation = unloadLocation
                    viewModel.unloadDate = toBackendDateTime(unloadCalendar)
                    viewModel.driverId = driverId
                    viewModel.vehicleId = vehicleId
                    viewModel.clientId = resolvedClientId
                    viewModel.updateTrip { result ->
                        isLoading = false
                        if (result is Resource.Success) {
                            confirmModalSuccess = true
                            confirmModalMessage = "Viaje actualizado correctamente"
                        } else {
                            confirmModalSuccess = false
                            confirmModalMessage = (result as? Resource.Error)?.message
                                ?: "No se pudo actualizar el viaje"
                        }
                        showConfirmModal = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = isFormValid(
                    name, type, weight, loadLocation, unloadLocation,
                    loadCalendar, unloadCalendar, driverId, vehicleId, resolvedClientId
                ) && !isLoading
            ) {
                Text(
                    "Guardar Cambios",
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
                    onValueChange = { if (it.length <= 60) name = it },
                    label = { Text("Nombre del Viaje") },
                    leadingIcon = { Icon(Icons.Filled.LocalShipping, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    supportingText = {
                        Text(
                            text = "${name.length}/60",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
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
                        leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
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
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        val filtered = it.filter { c -> c.isDigit() || c == '.' }
                        val dotCount = filtered.count { c -> c == '.' }
                        val parsed = filtered.toDoubleOrNull()
                        if (dotCount <= 1 && (filtered.isEmpty() || filtered.endsWith('.') ||
                                    (parsed != null && parsed <= 99999999.99))) {
                            weight = filtered
                        }
                    },
                    label = { Text("Peso (kg)") },
                    leadingIcon = { Icon(Icons.Filled.Scale, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = loadLocation,
                    onValueChange = { if (it.length <= 100) loadLocation = it },
                    label = { Text("Ubicación de Carga") },
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    supportingText = {
                        Text(
                            text = "${loadLocation.length}/100",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }

            item {
                Text("Fecha y hora de Carga", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                Button(
                    onClick = { showDateTimePicker(context, loadCalendar) { loadCalendar = it } },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (loadDateText.isBlank()) "Seleccionar carga" else loadDateText, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = unloadLocation,
                    onValueChange = { if (it.length <= 100) unloadLocation = it },
                    label = { Text("Ubicación de Descarga") },
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    supportingText = {
                        Text(
                            text = "${unloadLocation.length}/100",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }

            item {
                Text("Fecha y hora de Descarga", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                Button(
                    onClick = { showDateTimePicker(context, unloadCalendar) { unloadCalendar = it } },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (unloadDateText.isBlank()) "Seleccionar descarga" else unloadDateText, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Button(
                    onClick = { showDriverModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (driverName.isBlank()) "Seleccionar Conductor" else driverName, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            item {
                Button(
                    onClick = { showVehicleModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (vehicleName.isBlank()) "Seleccionar Vehículo" else vehicleName, color = MaterialTheme.colorScheme.onSurface)
                    }
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
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
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
                    isError = clientDniError.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
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
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                if (confirmModalSuccess) navController.popBackStack()
            },
            onDismiss = { showConfirmModal = false }
        )
    }

    if (showDriverModal) {
        EditDriverModal(
            viewModel = viewModel,
            entrepreneurId = Constants.ENTREPRENEUR_ID,
            currentDriverId = driverId,
            onDriverSelected = { id, selectedName ->
                driverId = id
                driverName = selectedName
                showDriverModal = false
            },
            onDismiss = { showDriverModal = false }
        )
    }

    if (showVehicleModal) {
        EditVehicleModal(
            viewModel = viewModel,
            entrepreneurId = Constants.ENTREPRENEUR_ID,
            currentVehicleId = vehicleId,
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
private fun EditDriverModal(
    viewModel: TripEditViewModel,
    entrepreneurId: Int,
    currentDriverId: Int,
    onDriverSelected: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var drivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        drivers = when (val result = viewModel.getDrivers(entrepreneurId)) {
            is Resource.Success -> result.data?.filter {
                it.state == "AVAILABLE" || it.id == currentDriverId
            } ?: emptyList()
            else -> emptyList()
        }
        isLoading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Conductor") },
        text = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (drivers.isEmpty()) {
                Text("No hay conductores disponibles", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(drivers) { driver ->
                        Text(
                            driver.name,
                            modifier = Modifier.fillMaxWidth().clickable { onDriverSelected(driver.id, driver.name) }.padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EditVehicleModal(
    viewModel: TripEditViewModel,
    entrepreneurId: Int,
    currentVehicleId: Int,
    onVehicleSelected: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        vehicles = when (val result = viewModel.getVehicles(entrepreneurId)) {
            is Resource.Success -> result.data?.filter {
                it.state == "AVAILABLE" || it.id == currentVehicleId
            } ?: emptyList()
            else -> emptyList()
        }
        isLoading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Vehículo") },
        text = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (vehicles.isEmpty()) {
                Text("No hay vehículos disponibles", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(vehicles) { vehicle ->
                        Text(
                            vehicle.model,
                            modifier = Modifier.fillMaxWidth().clickable { onVehicleSelected(vehicle.id, vehicle.model) }.padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun isFormValid(
    name: String, type: String, weight: String,
    loadLocation: String, unloadLocation: String,
    loadCalendar: Calendar?, unloadCalendar: Calendar?,
    driverId: Int, vehicleId: Int, resolvedClientId: Int
): Boolean =
    name.isNotBlank() && type.isNotBlank() &&
            weight.toDoubleOrNull()?.let { it > 0.0 } == true &&
            loadLocation.isNotBlank() && unloadLocation.isNotBlank() &&
            loadCalendar != null && unloadCalendar != null &&
            driverId > 0 && vehicleId > 0 && resolvedClientId > 0

private fun showDateTimePicker(
    context: android.content.Context,
    initial: Calendar?,
    onSelected: (Calendar) -> Unit
) {
    val start = initial ?: Calendar.getInstance()
    DatePickerDialog(
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
    ).show()
}

private fun toBackendDateTime(calendar: Calendar?): String {
    if (calendar == null) return ""
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return format.format(calendar.time)
}

private fun parseIsoToCalendar(isoDate: String): Calendar? {
    if (isoDate.isBlank()) return null
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = format.parse(isoDate) ?: return null
        Calendar.getInstance().apply { time = date }
    } catch (_: Exception) { null }
}
