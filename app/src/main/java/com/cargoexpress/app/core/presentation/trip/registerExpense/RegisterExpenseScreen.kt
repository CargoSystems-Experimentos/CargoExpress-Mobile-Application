package com.cargoexpress.app.core.presentation.trip.registerExpense

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.presentation.common.ConfirmationModal

private fun isDecimalValid(input: String): Boolean {
    val dotIdx = input.indexOf('.')
    return if (dotIdx == -1) input.length <= 8
    else input.indexOf('.', dotIdx + 1) == -1 && input.length - dotIdx - 1 <= 2 && dotIdx <= 8
}

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
    var isLoading by remember { mutableStateOf(false) }
    var showConfirmModal by remember { mutableStateOf(false) }
    var confirmModalSuccess by remember { mutableStateOf(false) }
    var confirmModalMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

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
                    Column {
                        Text(
                            "REGISTRAR GASTOS",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Lo que duele pero se anota",
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
                        viewModel.fuelAmount = fuelAmount.toDoubleOrNull() ?: 0.0
                        viewModel.fuelDescription = fuelDescription
                        viewModel.viaticsAmount = viaticsAmount.toDoubleOrNull() ?: 0.0
                        viewModel.viaticsDescription = viaticsDescription
                        viewModel.tollsAmount = tollsAmount.toDoubleOrNull() ?: 0.0
                        viewModel.tollsDescription = tollsDescription

                        viewModel.registerExpense { result ->
                            isLoading = false
                            if (result is Resource.Success && result.data != null) {
                                onExpenseRegistered(result.data)
                                confirmModalSuccess = true
                                confirmModalMessage = "Gasto registrado correctamente"
                            } else {
                                confirmModalSuccess = false
                                confirmModalMessage = (result as? Resource.Error)?.message
                                    ?: "No se pudo registrar el gasto"
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
                    "Registrar Gasto",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            //verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Combustible
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                Column(modifier = Modifier.padding(5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

    if (showConfirmModal) {
        ConfirmationModal(
            isSuccess = confirmModalSuccess,
            message = confirmModalMessage,
            onConfirm = {
                showConfirmModal = false
                if (confirmModalSuccess) {
                    fuelAmount = ""
                    fuelDescription = ""
                    viaticsAmount = ""
                    viaticsDescription = ""
                    tollsAmount = ""
                    tollsDescription = ""
                    navController.popBackStack()
                }
            },
            onDismiss = { showConfirmModal = false }
        )
    }
}
