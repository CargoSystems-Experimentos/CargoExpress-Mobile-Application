package com.cargoexpress.app.core.presentation.trip.registerExpense

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.cargoexpress.app.core.domain.Expense
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Resource

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterExpenseScreen(
    tripId: Int,
    viewModel: RegisterExpenseViewModel = viewModel(),
    onExpenseRegistered: (Expense) -> Unit
) {
    var fuelAmount by remember { mutableStateOf("") }
    var fuelDescription by remember { mutableStateOf("") }
    var viaticsAmount by remember { mutableStateOf("") }
    var viaticsDescription by remember { mutableStateOf("") }
    var tollsAmount by remember { mutableStateOf("") }
    var tollsDescription by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Expense") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputField(
                value = fuelAmount,
                label = "Monto de Combustible",
                onValueChange = { fuelAmount = it }
            )
            InputField(
                value = fuelDescription,
                label = "Descripción de Combustible",
                onValueChange = { fuelDescription = it }
            )
            InputField(
                value = viaticsAmount,
                label = "Monto de Viáticos",
                onValueChange = { viaticsAmount = it }
            )
            InputField(
                value = viaticsDescription,
                label = "Descripción de Viáticos",
                onValueChange = { viaticsDescription = it }
            )
            InputField(
                value = tollsAmount,
                label = "Monto de Peajes",
                onValueChange = { tollsAmount = it }
            )
            InputField(
                value = tollsDescription,
                label = "Descripción de Peajes",
                onValueChange = { tollsDescription = it }
            )

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFFEB3B))
            }

            Button(
                onClick = {
                    isLoading = true
                    val expense = Expense(
                        id = 0,
                        fuelAmount = fuelAmount.toDoubleOrNull() ?: 0.0,
                        fuelDescription = fuelDescription,
                        viaticsAmount = viaticsAmount.toDoubleOrNull() ?: 0.0,
                        viaticsDescription = viaticsDescription,
                        tollsAmount = tollsAmount.toDoubleOrNull() ?: 0.0,
                        tollsDescription = tollsDescription,
                        tripId = tripId
                    )
                    scope.launch {
                        val result = viewModel.registerExpense(expense)
                        isLoading = false
                        val message = if (result is Resource.Success) {
                            onExpenseRegistered(result.data!!)
                            "Expense registered successfully"
                        } else {
                            "Failed to register expense"
                        }
                        snackbarHostState.showSnackbar(message)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = !isLoading
            ) {
                Text("Register Expense", color = Color.Black)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(value: String, label: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color(0xFFFFEB3B),
            unfocusedIndicatorColor = Color.Gray
        ),
        textStyle = LocalTextStyle.current.copy(color = Color.Black)
    )
}