package com.cargoexpress.app.core.presentation.trip.registerTrip

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

private fun isWeightDecimalValid(input: String): Boolean {
    val dotIdx = input.indexOf('.')
    return if (dotIdx == -1) input.length <= 8
    else input.indexOf('.', dotIdx + 1) == -1 && input.length - dotIdx - 1 <= 2 && dotIdx <= 8
}

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

    var showLoadDatePicker by remember { mutableStateOf(false) }
    var showUnloadDatePicker by remember { mutableStateOf(false) }
    var tempLoadDateMillis by remember { mutableStateOf<Long?>(null) }
    var tempUnloadDateMillis by remember { mutableStateOf<Long?>(null) }

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
    val isWeightValid = weight.toDoubleOrNull()?.let { it > 0.0 } == true
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

    val isFormValid = isNameValid && isTypeValid && isWeightValid &&
            isLoadLocationValid && isUnloadLocationValid &&
            isDriverValid && isVehicleValid &&
            isLoadValid && isUnloadValid && isClientValid

    // Material3 DatePickerDialog — Carga
    if (showLoadDatePicker) {
        val minDateMillis = remember { Calendar.getInstance().timeInMillis }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = tempLoadDateMillis ?: minDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= minDateMillis
            }
        )
        DatePickerDialog(
            onDismissRequest = { showLoadDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    tempLoadDateMillis = millis
                    showLoadDatePicker = false
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            loadCalendar = cal
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showLoadDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // Material3 DatePickerDialog — Descarga
    if (showUnloadDatePicker) {
        val minDateMillis = remember { Calendar.getInstance().timeInMillis }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = tempUnloadDateMillis ?: minDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= minDateMillis
            }
        )
        DatePickerDialog(
            onDismissRequest = { showUnloadDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    tempUnloadDateMillis = millis
                    showUnloadDatePicker = false
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            unloadCalendar = cal
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showUnloadDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "NUEVO VIAJE",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Una nueva aventura sobre ruedas",
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
            Button(
                onClick = {
                    if (isFormValid) {
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

                        viewModel.registerTrip { result ->
                            isLoading = false
                            if (result is Resource.Success && result.data != null) {
                                onTripRegistered(result.data)
                                confirmModalSuccess = true
                                confirmModalMessage = "Viaje registrado correctamente"
                            } else {
                                confirmModalSuccess = false
                                confirmModalMessage = (result as? Resource.Error)?.message
                                    ?: "No se pudo registrar el viaje"
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Sección 1: DATOS DE CARGA ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = Color(0xFFFFEB3B), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Datos de Carga", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            if (it.length <= 60) { name = it; nameTouched = true }
                        },
                        label = { Text("Nombre del Viaje") },
                        leadingIcon = { Icon(Icons.Filled.LocalShipping, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        isError = showNameError,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                if (showNameError) Text("El nombre es obligatorio", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                                else Spacer(Modifier.weight(1f))
                                Text("${name.length}/60", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    )

                    val cargoTypes = listOf("ESTANDAR", "FRAGIL", "PESADO", "VALIOSO", "URGENTE", "PERECIBLE")
                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Carga") },
                            leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            isError = showTypeError,
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                            cargoTypes.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = { type = option; typeTouched = true; typeExpanded = false }
                                )
                            }
                        }
                    }
                    if (showTypeError) Text("El tipo de carga es obligatorio", color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            if (filtered.isEmpty() || isWeightDecimalValid(filtered)) { weight = filtered; weightTouched = true }
                        },
                        label = { Text("Peso (kg)") },
                        leadingIcon = { Icon(Icons.Filled.Scale, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        isError = showWeightError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showWeightError) Text("El peso es obligatorio y debe ser mayor a 0", color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                }
            }

            // ── Sección 2: PARTES RESPONSABLES ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.People, contentDescription = null, tint = Color(0xFFFFEB3B), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Partes Responsables", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = { showDriverModal = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isDriverValid) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (driverName.isBlank()) "Seleccionar Conductor" else driverName,
                                color = if (driverName.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (!isDriverValid) Text("Debes seleccionar un conductor", color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))

                    OutlinedButton(
                        onClick = { showVehicleModal = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (vehicleName.isBlank()) "Seleccionar Vehículo" else vehicleName,
                                color = if (vehicleName.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (!isVehicleValid) Text("Debes seleccionar un vehículo", color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))

                    OutlinedTextField(
                        value = clientDni,
                        onValueChange = {
                            clientDni = it.filter { c -> c.isDigit() }.take(8)
                            resolvedClientId = 0
                            clientFoundName = ""
                            clientDniError = ""
                        },
                        label = { Text("DNI del Cliente") },
                        leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
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
                                if (isDniValidating) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                else Icon(Icons.Filled.Search, contentDescription = "Verificar DNI")
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        isError = clientDniError.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    when {
                        clientFoundName.isNotBlank() -> Text(
                            "Cliente: $clientFoundName",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        clientDniError.isNotBlank() -> Text(
                            clientDniError,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // ── Sección 3: RUTA Y FECHAS ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Map, contentDescription = null, tint = Color(0xFFFFEB3B), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ruta y Fechas", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedTextField(
                        value = loadLocation,
                        onValueChange = {
                            if (it.length <= 100) { loadLocation = it; loadLocationTouched = true }
                        },
                        label = { Text("Ubicación de Carga") },
                        leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        isError = showLoadLocationError,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                if (showLoadLocationError) Text("Obligatorio", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                                else Spacer(Modifier.weight(1f))
                                Text("${loadLocation.length}/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    )

                    Box(modifier = Modifier.fillMaxWidth().clickable { showLoadDatePicker = true }) {
                        OutlinedTextField(
                            value = loadDateText,
                            onValueChange = {},
                            label = { Text("Fecha y hora de Carga") },
                            leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null) },
                            placeholder = { Text("Seleccionar fecha y hora") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (!isLoadValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    if (!isLoadValid) Text("Debes seleccionar la fecha y hora de carga", color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))

                    OutlinedTextField(
                        value = unloadLocation,
                        onValueChange = {
                            if (it.length <= 100) { unloadLocation = it; unloadLocationTouched = true }
                        },
                        label = { Text("Ubicación de Descarga") },
                        leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        isError = showUnloadLocationError,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                if (showUnloadLocationError) Text("Obligatorio", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                                else Spacer(Modifier.weight(1f))
                                Text("${unloadLocation.length}/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    )

                    Box(modifier = Modifier.fillMaxWidth().clickable { showUnloadDatePicker = true }) {
                        OutlinedTextField(
                            value = unloadDateText,
                            onValueChange = {},
                            label = { Text("Fecha y hora de Descarga") },
                            leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null) },
                            placeholder = { Text("Seleccionar fecha y hora") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = if (!isUnloadValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    if (!isUnloadValid) Text("Debes seleccionar la fecha y hora de descarga", color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showConfirmModal) {
        ConfirmationModal(
            isSuccess = confirmModalSuccess,
            message = confirmModalMessage,
            onConfirm = {
                showConfirmModal = false
                if (confirmModalSuccess) {
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
                is Resource.Success -> result.data?.filter { it.state == "AVAILABLE" } ?: emptyList()
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
            } else if (drivers.isEmpty()) {
                Text("No hay conductores disponibles", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(drivers) { driver ->
                        Text(
                            driver.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDriverSelected(driver.id, driver.name) }
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
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
                is Resource.Success -> result.data?.filter { it.state == "AVAILABLE" } ?: emptyList()
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
            } else if (vehicles.isEmpty()) {
                Text("No hay vehículos disponibles", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(vehicles) { vehicle ->
                        Text(
                            vehicle.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onVehicleSelected(vehicle.id, vehicle.name) }
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun toBackendDateTime(calendar: Calendar?): String {
    if (calendar == null) return ""
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return format.format(calendar.time)
}
