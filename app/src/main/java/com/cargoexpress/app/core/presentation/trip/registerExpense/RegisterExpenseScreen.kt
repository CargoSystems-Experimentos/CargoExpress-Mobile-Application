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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.common.Resource
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterExpenseScreen(
    tripId: Int,
    navController: NavController,
    viewModel: RegisterExpenseViewModel = viewModel(),
    onExpenseRegistered: (Expense) -> Unit
) {
    var fuelAmount by remember { mutableStateOf("") }
    var fuelDescription by remember { mutableStateOf("") }
    var viaticsAmount by remember { mutableStateOf("") }
    var viaticsDescription by remember { mutableStateOf("") }
    var tollsAmount by remember { mutableStateOf("") }
    var tollsDescription by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Validaciones
    val isFuelAmountValid = fuelAmount.isNotBlank() && fuelAmount.matches(Regex("""^\d+(\.\d{1,2})?$"""))
    val isFuelDescriptionValid = fuelDescription.isNotBlank()
    val isViaticsAmountValid = viaticsAmount.isNotBlank() && viaticsAmount.matches(Regex("""^\d+(\.\d{1,2})?$"""))
    val isViaticsDescriptionValid = viaticsDescription.isNotBlank()
    val isTollsAmountValid = tollsAmount.isNotBlank() && tollsAmount.matches(Regex("""^\d+(\.\d{1,2})?$"""))
    val isTollsDescriptionValid = tollsDescription.isNotBlank()

    val isFormValid = isFuelAmountValid && isFuelDescriptionValid &&
            isViaticsAmountValid && isViaticsDescriptionValid &&
            isTollsAmountValid && isTollsDescriptionValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Gasto") },
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
            Text(
                text = "Registrar Gasto",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Selector de moneda
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Moneda",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("USD", "PEN", "EUR").forEach { currency ->
                            FilterChip(
                                selected = selectedCurrency == currency,
                                onClick = { selectedCurrency = currency },
                                label = { Text(currency) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

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
                        onValueChange = { fuelAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Monto (0.00)") },
                        isError = fuelAmount.isNotBlank() && !isFuelAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = fuelDescription,
                        onValueChange = { fuelDescription = it },
                        label = { Text("Descripción") },
                        isError = fuelDescription.isNotBlank() && !isFuelDescriptionValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
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
                        onValueChange = { viaticsAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Monto (0.00)") },
                        isError = viaticsAmount.isNotBlank() && !isViaticsAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = viaticsDescription,
                        onValueChange = { viaticsDescription = it },
                        label = { Text("Descripción") },
                        isError = viaticsDescription.isNotBlank() && !isViaticsDescriptionValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
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
                        onValueChange = { tollsAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Monto (0.00)") },
                        isError = tollsAmount.isNotBlank() && !isTollsAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = tollsDescription,
                        onValueChange = { tollsDescription = it },
                        label = { Text("Descripción") },
                        isError = tollsDescription.isNotBlank() && !isTollsDescriptionValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
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
                    isLoading = true
                    val expense = Expense(
                        id = 0,
                        fuelAmount = fuelAmount.toDoubleOrNull() ?: 0.0,
                        fuelDescription = "$selectedCurrency - $fuelDescription",
                        viaticsAmount = viaticsAmount.toDoubleOrNull() ?: 0.0,
                        viaticsDescription = "$selectedCurrency - $viaticsDescription",
                        tollsAmount = tollsAmount.toDoubleOrNull() ?: 0.0,
                        tollsDescription = "$selectedCurrency - $tollsDescription",
                        tripId = tripId
                    )

                    val expenseJson = JSONObject().apply {
                        put("id", expense.id)
                        put("fuelAmount", expense.fuelAmount)
                        put("fuelAmount_type", "Double")
                        put("fuelDescription", expense.fuelDescription)
                        put("fuelDescription_type", "String")
                        put("viaticsAmount", expense.viaticsAmount)
                        put("viaticsAmount_type", "Double")
                        put("viaticsDescription", expense.viaticsDescription)
                        put("viaticsDescription_type", "String")
                        put("tollsAmount", expense.tollsAmount)
                        put("tollsAmount_type", "Double")
                        put("tollsDescription", expense.tollsDescription)
                        put("tollsDescription_type", "String")
                        put("tripId", expense.tripId)
                        put("tripId_type", "Int")
                    }

                    Log.d("RegisterExpenseScreen", "Datos del Gasto:\n${expenseJson.toString(2)}")

                    isLoading = false

                    /*
                    scope.launch {
                        val result = viewModel.registerExpense(expense)
                        isLoading = false
                        val message = if (result is Resource.Success) {
                            onExpenseRegistered(result.data!!)
                            "Gasto registrado correctamente"
                        } else {
                            "No se pudo registrar el gasto"
                        }
                        snackbarHostState.showSnackbar(message)
                    }
                    */
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = isFormValid && !isLoading
            ) {
                Text("Registrar Gasto", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}