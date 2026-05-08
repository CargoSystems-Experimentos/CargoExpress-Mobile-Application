package com.cargoexpress.app.core.presentation.driver.driverList.registerDriver

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
import com.cargoexpress.app.core.domain.Driver
import pe.edu.upc.appturismo.common.Resource
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@Composable
fun RegisterDriverScreen(
    navController: NavController,
    viewModel: RegisterDriverViewModel = viewModel(),
    onDriverRegistered: (Driver) -> Unit
) {

    var name by remember { mutableStateOf(viewModel.name) }
    var dni by remember { mutableStateOf(viewModel.dni) }
    var license by remember { mutableStateOf(viewModel.license) }
    var contactNumber by remember { mutableStateOf(viewModel.contactNumber) }


    var nameError by remember { mutableStateOf<String?>(null) }
    var dniError by remember { mutableStateOf<String?>(null) }
    var licenseError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }


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
                    text = "Registrar Nuevo Conductor",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            InputField(
                value = name,
                label = "Nombre",
                onValueChange = {
                    name = it
                    nameError = if (it.isBlank()) "El nombre es obligatorio" else null
                },
                error = nameError
            )
            InputField(
                value = dni,
                label = "DNI",
                onValueChange = {
                    dni = it
                    dniError = when {
                        it.isBlank() -> "El DNI es obligatorio"
                        it.length != 8 -> "El DNI debe tener 8 dígitos"
                        else -> null
                    }
                },
                error = dniError
            )
            InputField(
                value = license,
                label = "Licencia",
                onValueChange = {
                    license = it
                    licenseError = if (it.isBlank()) "La licencia es obligatoria" else null
                },
                error = licenseError
            )
            InputField(
                value = contactNumber,
                label = "Número de contacto",
                onValueChange = {
                    contactNumber = it
                    contactError = when {
                        it.isBlank() -> "El número de contacto es obligatorio"
                        it.length < 9 -> "El número de contacto debe tener al menos 9 dígitos"
                        !it.all { char -> char.isDigit() } -> "El número de contacto debe ser numérico"
                        else -> null
                    }
                },
                error = contactError
            )

            Spacer(modifier = Modifier.height(16.dp))


            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFFEB3B))
            }

            Button(
                onClick = {

                    nameError = if (name.isBlank()) "El nombre es obligatorio" else null
                    dniError = when {
                        dni.isBlank() -> "El DNI es obligatorio"
                        dni.length != 8 -> "El DNI debe tener 8 dígitos"
                        else -> null
                    }
                    licenseError = if (license.isBlank()) "La licencia es obligatoria" else null
                    contactError = when {
                        contactNumber.isBlank() -> "El número de contacto es obligatorio"
                        contactNumber.length < 9 -> "Debe tener al menos 9 dígitos"
                        !contactNumber.all { char -> char.isDigit() } -> "Debe ser numérico"
                        else -> null
                    }


                    val valid = listOf(nameError, dniError, licenseError, contactError).all { it == null }

                    if (valid) {
                        isLoading = true
                        viewModel.name = name
                        viewModel.dni = dni
                        viewModel.license = license
                        viewModel.contactNumber = contactNumber

                        viewModel.registerDriver { result ->
                            isLoading = false
                            val message = if (result is Resource.Success && result.data != null) {
                                onDriverRegistered(result.data)


                                name = ""
                                dni = ""
                                license = ""
                                contactNumber = ""
                                navController.navigate("drivers")
                                "Conductor registrado correctamente"
                            } else {
                                "No se pudo registrar al conductor"
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
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = !isLoading
            ) {
                Text("Registrar Conductor", color = Color.Black)
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
