package com.cargoexpress.app.core.presentation.trip.editExpense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.presentation.common.ConfirmationModal

private fun isDecimalValid(input: String): Boolean {
    val dotIdx = input.indexOf('.')
    return if (dotIdx == -1) input.length <= 8
    else input.indexOf('.', dotIdx + 1) == -1 && input.length - dotIdx - 1 <= 2 && dotIdx <= 8
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    expenseId: Int,
    navController: NavController,
    viewModel: EditExpenseViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var fuelAmount by remember { mutableStateOf("") }
    var fuelDescription by remember { mutableStateOf("") }
    var viaticsAmount by remember { mutableStateOf("") }
    var viaticsDescription by remember { mutableStateOf("") }
    var tollsAmount by remember { mutableStateOf("") }
    var tollsDescription by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    LaunchedEffect(expenseId) {
        viewModel.loadExpense(expenseId)
    }

    LaunchedEffect(uiState.expense) {
        uiState.expense?.let { expense ->
            fuelAmount = expense.fuelAmount.toString()
            fuelDescription = expense.fuelDescription
            viaticsAmount = expense.viaticsAmount.toString()
            viaticsDescription = expense.viaticsDescription
            tollsAmount = expense.tollsAmount.toString()
            tollsDescription = expense.tollsDescription
        }
    }

    val isFuelAmountValid = fuelAmount.isNotBlank() && fuelAmount.toDoubleOrNull() != null && (fuelAmount.toDoubleOrNull() ?: -1.0) >= 0.0
    val isFuelDescriptionValid = fuelDescription.isNotBlank()
    val isViaticsAmountValid = viaticsAmount.isNotBlank() && viaticsAmount.toDoubleOrNull() != null && (viaticsAmount.toDoubleOrNull() ?: -1.0) >= 0.0
    val isViaticsDescriptionValid = viaticsDescription.isNotBlank()
    val isTollsAmountValid = tollsAmount.isNotBlank() && tollsAmount.toDoubleOrNull() != null && (tollsAmount.toDoubleOrNull() ?: -1.0) >= 0.0
    val isTollsDescriptionValid = tollsDescription.isNotBlank()

    val isFormValid = isFuelAmountValid && isFuelDescriptionValid &&
            isViaticsAmountValid && isViaticsDescriptionValid &&
            isTollsAmountValid && isTollsDescriptionValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Gasto",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
                onClick = { if (isFormValid) showWarningDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = isFormValid && !isLoading
            ) {
                Text(
                    "Actualizar Gasto",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFFEB3B))
            }
            return@Scaffold
        }

        if (uiState.message.isNotBlank() && uiState.expense == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

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
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            if (filtered.isEmpty() || isDecimalValid(filtered)) fuelAmount = filtered
                        },
                        label = { Text("Monto Combustible (USD)") },
                        isError = fuelAmount.isNotBlank() && !isFuelAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = fuelDescription,
                        onValueChange = { if (it.length <= 200) fuelDescription = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = {
                            Text(
                                text = "${fuelDescription.length}/200",
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
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            if (filtered.isEmpty() || isDecimalValid(filtered)) viaticsAmount = filtered
                        },
                        label = { Text("Monto Viáticos (USD)") },
                        isError = viaticsAmount.isNotBlank() && !isViaticsAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = viaticsDescription,
                        onValueChange = { if (it.length <= 200) viaticsDescription = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = {
                            Text(
                                text = "${viaticsDescription.length}/200",
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
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            if (filtered.isEmpty() || isDecimalValid(filtered)) tollsAmount = filtered
                        },
                        label = { Text("Monto Peajes (USD)") },
                        isError = tollsAmount.isNotBlank() && !isTollsAmountValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = tollsDescription,
                        onValueChange = { if (it.length <= 200) tollsDescription = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = {
                            Text(
                                text = "${tollsDescription.length}/200",
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("Advertencia", fontWeight = FontWeight.Bold) },
            text = { Text("Este gasto solo puede editarse una vez. ¿Deseas continuar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showWarningDialog = false
                        isLoading = true
                        viewModel.fuelAmount = fuelAmount.toDoubleOrNull() ?: 0.0
                        viewModel.fuelDescription = fuelDescription
                        viewModel.viaticsAmount = viaticsAmount.toDoubleOrNull() ?: 0.0
                        viewModel.viaticsDescription = viaticsDescription
                        viewModel.tollsAmount = tollsAmount.toDoubleOrNull() ?: 0.0
                        viewModel.tollsDescription = tollsDescription
                        viewModel.updateExpense(expenseId) { result ->
                            isLoading = false
                            if (result is Resource.Success) {
                                confirmModalSuccess = true
                                confirmModalMessage = "Gasto actualizado correctamente"
                            } else {
                                confirmModalSuccess = false
                                confirmModalMessage = (result as? Resource.Error)?.message
                                    ?: "No se pudo actualizar el gasto"
                            }
                            showConfirmModal = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
                ) {
                    Text("Confirmar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWarningDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showConfirmModal) {
        ConfirmationModal(
            isSuccess = confirmModalSuccess,
            message = confirmModalMessage,
            onConfirm = {
                showConfirmModal = false
                if (confirmModalSuccess) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("expense_updated", true)
                    navController.popBackStack()
                }
            },
            onDismiss = { showConfirmModal = false }
        )
    }
}
