package com.cargoexpress.app.core.presentation.driver.driverList.registerDriver

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
import com.cargoexpress.app.core.domain.Driver
import com.cargoexpress.app.core.common.Resource
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

    // Validación DNI: exactamente 8 números
    val isDniValid = dni.length == 8 && dni.all { it.isDigit() }
    val showDniError = dni.isNotBlank() && !isDniValid

    // Validación Licencia: Q seguido de 8 números (9 caracteres total)
    val isLicenseValid = license.length == 9 && license.startsWith("Q") && license.drop(1).all { it.isDigit() }
    val showLicenseError = license.isNotBlank() && !isLicenseValid

    // Validación Número de contacto: exactamente 9 números
    val isContactValid = contactNumber.length == 9 && contactNumber.all { it.isDigit() }
    val showContactError = contactNumber.isNotBlank() && !isContactValid

    // Validación Nombre: no vacío
    val isNameValid = name.isNotBlank()

    // Validación general del formulario
    val isFormValid = isNameValid && isDniValid && isLicenseValid && isContactValid

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
                    text = "Registrar Conductor",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre Input
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = if (it.isBlank()) "El nombre es obligatorio" else null
                },
                label = { Text("Nombre") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Nombre") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (nameError != null) {
                Text(
                    text = nameError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // DNI Input
            OutlinedTextField(
                value = dni,
                onValueChange = {
                    // Solo acepta dígitos y máximo 8 caracteres
                    dni = it.filter { ch -> ch.isDigit() }.take(8)
                    dniError = when {
                        it.isNotBlank() && it.length != 8 -> "El número de DNI no es válido"
                        !it.all { ch -> ch.isDigit() || ch.isLetter() } -> "El número de DNI no es válido"
                        else -> null
                    }
                },
                label = { Text("DNI") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Info, contentDescription = "DNI") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                isError = showDniError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showDniError) {
                Text(
                    text = "El número de DNI no es válido",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Licencia Input
            var rawLicense by remember { mutableStateOf("") }

            OutlinedTextField(
                value = "Q" + rawLicense,
                onValueChange = {
                    // Mantiene la Q al inicio y solo permite editar los 8 dígitos después
                    val withoutQ = it.removePrefix("Q")
                    rawLicense = withoutQ.filter { ch -> ch.isDigit() }.take(8)
                    license = "Q" + rawLicense
                    licenseError = when {
                        rawLicense.isNotBlank() && rawLicense.length != 8 -> "El número de licencia no es válido"
                        rawLicense.isNotBlank() && !rawLicense.all { ch -> ch.isDigit() } -> "El número de licencia no es válido"
                        else -> null
                    }
                },
                label = { Text("Licencia") },
                leadingIcon = { Icon(imageVector = Icons.Filled.TimeToLeave, contentDescription = "Licencia") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                isError = showLicenseError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (showLicenseError) {
                Text(
                    text = "El número de licencia no es válido",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Número de contacto Input
            var rawContactNumber by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Peru",
                    modifier = Modifier.padding(end = 8.dp),
                    color = Color.Gray
                )
                OutlinedTextField(
                    value = formatPhone(rawContactNumber),
                    onValueChange = {
                        // Solo acepta dígitos y máximo 9 caracteres
                        rawContactNumber = it.filter { ch -> ch.isDigit() }.take(9)
                        contactNumber = rawContactNumber
                        contactError = when {
                            rawContactNumber.isNotBlank() && rawContactNumber.length != 9 -> "El número de contacto no es válido"
                            !rawContactNumber.all { ch -> ch.isDigit() } -> "El número de contacto no es válido"
                            else -> null
                        }
                    },
                    label = { Text("Número de contacto") },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "Número de contacto") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    maxLines = 1,
                    isError = showContactError,
                    modifier = Modifier.weight(1f)
                )
            }
            if (showContactError) {
                Text(
                    text = "El número de contacto no es válido",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Left
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFFEB3B))
            }

            Button(
                onClick = {
                    nameError = if (name.isBlank()) "El nombre es obligatorio" else null
                    dniError = if (dni.length != 8 || !dni.all { it.isDigit() }) "El número de DNI no es válido" else null
                    licenseError = if (license.length != 9 || !license.startsWith("Q") || !license.drop(1).all { it.isDigit() }) "El número de licencia no es válido" else null
                    contactError = if (contactNumber.length != 9 || !contactNumber.all { it.isDigit() }) "El número de contacto no es válido" else null

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = isFormValid && !isLoading
            ) {
                Text(
                    "Registrar Conductor",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatPhone(phone: String): String {
    return phone.chunked(3).joinToString("-")
}



