package com.cargoexpress.app.core.presentation.record.registerExpense

/*@Composable
fun RegisterExpenseScreen(
    viewModel: RegisterExpenseViewModel = viewModel(),
    onExpenseRegistered: (Expense) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Column(modifier = Modifier.padding(16.dp)) {
        InputField(
            value = viewModel.fuelAmount.toString(),
            label = "Fuel Amount",
            onValueChange = { viewModel.fuelAmount = it.toDoubleOrNull() ?: 0.0 },
            error = if (viewModel.fuelAmount == 0.0) "Fuel amount is required" else null
        )
        InputField(
            value = viewModel.fuelDescription,
            label = "Fuel Description",
            onValueChange = { viewModel.fuelDescription = it },
            error = if (viewModel.fuelDescription.isBlank()) "Fuel description is required" else null
        )
        InputField(
            value = viewModel.viaticsAmount.toString(),
            label = "Viatics Amount",
            onValueChange = { viewModel.viaticsAmount = it.toDoubleOrNull() ?: 0.0 },
            error = if (viewModel.viaticsAmount == 0.0) "Viatics amount is required" else null
        )
        InputField(
            value = viewModel.viaticsDescription,
            label = "Viatics Description",
            onValueChange = { viewModel.viaticsDescription = it },
            error = if (viewModel.viaticsDescription.isBlank()) "Viatics description is required" else null
        )
        InputField(
            value = viewModel.tollsAmount.toString(),
            label = "Tolls Amount",
            onValueChange = { viewModel.tollsAmount = it.toDoubleOrNull() ?: 0.0 },
            error = if (viewModel.tollsAmount == 0.0) "Tolls amount is required" else null
        )
        InputField(
            value = viewModel.tollsDescription,
            label = "Tolls Description",
            onValueChange = { viewModel.tollsDescription = it },
            error = if (viewModel.tollsDescription.isBlank()) "Tolls description is required" else null
        )
        InputField(
            value = viewModel.tripId.toString(),
            label = "Trip ID",
            onValueChange = { viewModel.tripId = it.toIntOrNull() ?: 0 },
            error = if (viewModel.tripId == 0) "Trip ID is required" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFFFFEB3B))
        }

        Button(
            onClick = {
                isLoading = true
                viewModel.registerExpense { result ->
                    isLoading = false
                    val message = if (result is Resource.Success && result.data != null) {
                        onExpenseRegistered(result.data)
                        "Expense registered successfully"
                    } else {
                        "Failed to register expense"
                    }

                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
            enabled = !isLoading
        ) {
            Text("Register Expense", color = Color.Black)
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
}*/