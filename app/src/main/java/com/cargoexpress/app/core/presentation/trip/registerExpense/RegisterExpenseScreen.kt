package com.cargoexpress.app.core.presentation.trip.registerExpense

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.common.Resource
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterExpenseScreen(
    navController: NavController,
    viewModel: RegisterExpenseViewModel = viewModel(),
    onExpenseRegistered: (Expense) -> Unit
) {

    var fuelAmount by remember { mutableStateOf("") }
    var fuelDescription by remember { mutableStateOf(viewModel.fuelDescription) }
    var viaticsAmount by remember { mutableStateOf("") }
    var viaticsDescription by remember { mutableStateOf(viewModel.viaticsDescription) }
    var tollsAmount by remember { mutableStateOf("") }
    var tollsDescription by remember { mutableStateOf(viewModel.tollsDescription) }
    //var selectedCurrency by remember { mutableStateOf("USD") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Validaciones
    val isFuelAmountValid = fuelAmount.isNotBlank() && fuelAmount.toIntOrNull() != null && fuelAmount.toInt() >= 0
    val isFuelDescriptionValid = fuelDescription.isNotBlank()
    val isViaticsAmountValid = viaticsAmount.isNotBlank() && viaticsAmount.toIntOrNull() != null && viaticsAmount.toInt() >= 0
    val isViaticsDescriptionValid = viaticsDescription.isNotBlank()
    val isTollsAmountValid = tollsAmount.isNotBlank() && tollsAmount.toIntOrNull() != null && tollsAmount.toInt() >= 0
    val isTollsDescriptionValid = tollsDescription.isNotBlank()

    val isFormValid = isFuelAmountValid && isFuelDescriptionValid &&
            isViaticsAmountValid && isViaticsDescriptionValid &&
            isTollsAmountValid && isTollsDescriptionValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Gasto", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retroceder")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Combustible
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = Color(0xFFFFEB3B), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Combustible", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedTextField(
                        value = fuelAmount,
                        onValueChange = { fuelAmount = it.filter { c -> c.isDigit() }.take(5) },
                        label = { Text("Monto Combustible (USD)") },
                        isError = !isFuelAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = fuelDescription,
                        onValueChange = { if (it.length <= 100) fuelDescription = it },
                        label = { Text("Descripción") },
                        isError = fuelDescription.isNotBlank() && !isFuelDescriptionValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        supportingText = {
                            Text(
                                text = "${fuelDescription.length}/100",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            // Viáticos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFFFFEB3B), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Viáticos", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedTextField(
                        value = viaticsAmount,
                        onValueChange = { viaticsAmount = it.filter { c -> c.isDigit() }.take(5) },
                        label = { Text("Monto Viáticos (USD)") },
                        isError = !isViaticsAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = viaticsDescription,
                        onValueChange = { if (it.length <= 100) viaticsDescription = it },
                        label = { Text("Descripción") },
                        isError = viaticsDescription.isNotBlank() && !isViaticsDescriptionValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        supportingText = {
                            Text(
                                text = "${viaticsDescription.length}/100",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            // Peajes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddRoad, contentDescription = null, tint = Color(0xFFFFEB3B), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Peajes", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedTextField(
                        value = tollsAmount,
                        onValueChange = { tollsAmount = it.filter { c -> c.isDigit() }.take(5) },
                        label = { Text("Monto Peajes (USD)") },
                        isError = !isTollsAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = tollsDescription,
                        onValueChange = { if (it.length <= 100) tollsDescription = it },
                        label = { Text("Descripción") },
                        isError = tollsDescription.isNotBlank() && !isTollsDescriptionValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        supportingText = {
                            Text(
                                text = "${tollsDescription.length}/100",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }

            Button(
                onClick = {
                    val fuelAmountError = if (fuelAmount.isBlank() || (fuelAmount.toIntOrNull() ?: 0) <= 0) "El monto de combustible es obligatorio" else null
                    val fuelDescriptionError = if (fuelDescription.isBlank()) "La descripción de combustible es obligatoria" else null
                    val viaticsAmountError = if (viaticsAmount.isBlank() || (viaticsAmount.toIntOrNull() ?: 0) <= 0) "El monto de viáticos es obligatorio" else null
                    val viaticsDescriptionError = if (viaticsDescription.isBlank()) "La descripción de viáticos es obligatoria" else null
                    val tollsAmountError = if (tollsAmount.isBlank() || (tollsAmount.toIntOrNull() ?: 0) <= 0) "El monto de peajes es obligatorio" else null
                    val tollsDescriptionError = if (tollsDescription.isBlank()) "La descripción de peajes es obligatoria" else null

                    val valid = listOf(
                        fuelAmountError, fuelDescriptionError,
                        viaticsAmountError, viaticsDescriptionError,
                        tollsAmountError, tollsDescriptionError
                    ).all { it == null }

                    if (valid) {
                        isLoading = true
                        viewModel.fuelAmount = fuelAmount.toIntOrNull() ?: 0
                        viewModel.fuelDescription = "USD - $fuelDescription"
                        viewModel.viaticsAmount = viaticsAmount.toIntOrNull() ?: 0
                        viewModel.viaticsDescription = "USD - $viaticsDescription"
                        viewModel.tollsAmount = tollsAmount.toIntOrNull() ?: 0
                        viewModel.tollsDescription = "USD - $tollsDescription"

                        viewModel.registerExpense { result ->
                            isLoading = false
                            val message = if (result is Resource.Success && result.data != null) {
                                onExpenseRegistered(result.data)

                                fuelAmount = ""
                                fuelDescription = ""
                                viaticsAmount = ""
                                viaticsDescription = ""
                                tollsAmount = ""
                                tollsDescription = ""
                                navController.popBackStack()
                                "Gasto registrado correctamente"
                            } else {
                                "No se pudo registrar el gasto"
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
                    "Registrar Gasto",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}