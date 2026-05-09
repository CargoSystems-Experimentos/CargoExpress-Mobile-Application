package com.cargoexpress.app.core.presentation.trip.registerTrip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterTripScreen(
    viewModel: RegisterTripViewModel = viewModel(),
    onTripRegistered: (Trip) -> Unit
) {
    var tripName by remember { mutableStateOf("") }
    var cargoType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var loadLocation by remember { mutableStateOf("") }
    var loadDate by remember { mutableStateOf("") }
    var loadTime by remember { mutableStateOf("") }
    var unloadLocation by remember { mutableStateOf("") }
    var unloadDate by remember { mutableStateOf("") }
    var unloadTime by remember { mutableStateOf("") }
    var driverId by remember { mutableStateOf("") }
    var vehicleId by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var entrepreneurId by remember { mutableStateOf(Constants.ENTREPRENEUR_ID.toString()) }
    var isLoading by remember { mutableStateOf(false) }

    var tripNameError by remember { mutableStateOf<String?>(null) }
    var cargoTypeError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val context = LocalContext.current


    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected(timeFormatter.format(calendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    tripNameError = if (tripName.isBlank()) "Trip name is required" else null
                    cargoTypeError = if (cargoType.isBlank()) "Cargo type is required" else null
                    weightError = if (weight.isBlank() || weight.toFloatOrNull() == null) "Weight must be a number" else null

                    val isValid = listOf(tripNameError, cargoTypeError, weightError).all { it == null }

                    if (isValid) {
                        isLoading = true
                        viewModel.tripName = tripName
                        viewModel.cargoType = cargoType
                        viewModel.weight = weight.toIntOrNull() ?: 0
                        viewModel.loadLocation = loadLocation
                        viewModel.loadDate = isoFormatter.format(dateFormatter.parse(loadDate)!!).substring(0, 10) + "T" + loadTime + ":00"
                        viewModel.unloadLocation = unloadLocation
                        viewModel.unloadDate = isoFormatter.format(dateFormatter.parse(unloadDate)!!).substring(0, 10) + "T" + unloadTime + ":00"
                        viewModel.driverId = driverId.toIntOrNull() ?: 0
                        viewModel.vehicleId = vehicleId.toIntOrNull() ?: 0
                        viewModel.clientId = clientId.toIntOrNull() ?: 0

                        viewModel.registerTrip { result ->
                            isLoading = false
                            val message = if (result is Resource.Success && result.data != null) {
                                onTripRegistered(result.data)
                                tripName = ""
                                cargoType = ""
                                weight = ""
                                loadLocation = ""
                                loadDate = ""
                                loadTime = ""
                                unloadLocation = ""
                                unloadDate = ""
                                unloadTime = ""
                                driverId = ""
                                vehicleId = ""
                                clientId = ""
                                entrepreneurId = Constants.ENTREPRENEUR_ID.toString()
                                "Trip registered successfully"
                            } else {
                                "Viaje registrado correctamente"
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please complete all fields correctly")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = !isLoading
            ) {
                Text("Register Trip", color = Color.Black)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InputField(
                value = tripName,
                label = "Trip Name",
                onValueChange = {
                    tripName = it
                    tripNameError = if (it.isBlank()) "Trip name is required" else null
                },
                error = tripNameError
            )
            InputField(
                value = cargoType,
                label = "Cargo Type",
                onValueChange = {
                    cargoType = it
                    cargoTypeError = if (it.isBlank()) "Cargo type is required" else null
                },
                error = cargoTypeError
            )
            InputField(
                value = weight,
                label = "Weight",
                onValueChange = {
                    weight = it
                    weightError = if (it.isBlank() || it.toFloatOrNull() == null) "Weight must be a number" else null
                },
                error = weightError
            )
            InputField(value = loadLocation, label = "Load Location", onValueChange = { loadLocation = it }, error = null)

            OutlinedButton(
                onClick = { showDatePicker { loadDate = it } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (loadDate.isEmpty()) "Select Load Date" else "Load Date: $loadDate")
            }

            OutlinedButton(
                onClick = { showTimePicker { loadTime = it } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (loadTime.isEmpty()) "Select Load Time" else "Load Time: $loadTime")
            }

            InputField(value = unloadLocation, label = "Unload Location", onValueChange = { unloadLocation = it }, error = null)

            OutlinedButton(
                onClick = { showDatePicker { unloadDate = it } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (unloadDate.isEmpty()) "Select Unload Date" else "Unload Date: $unloadDate")
            }

            OutlinedButton(
                onClick = { showTimePicker { unloadTime = it } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (unloadTime.isEmpty()) "Select Unload Time" else "Unload Time: $unloadTime")
            }

            InputField(value = driverId, label = "Driver ID", onValueChange = { driverId = it }, error = null)
            InputField(value = vehicleId, label = "Vehicle ID", onValueChange = { vehicleId = it }, error = null)
            InputField(value = clientId, label = "Client ID", onValueChange = { clientId = it }, error = null)
            InputField(value = entrepreneurId, label = "Entrepreneur ID", onValueChange = { entrepreneurId = it }, error = null)

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFFEB3B))
            }
        }
    }
}

@Composable
fun InputField(value: String, label: String, onValueChange: (String) -> Unit, error: String?) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
