package com.cargoexpress.app.core.presentation.driver.driverList


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.common.Constants

@Composable
fun DriverListScreen(viewModel: DriverListViewModel = viewModel(), navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.getDriverList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(end = 8.dp)
                        .shadow(4.dp, RoundedCornerShape(24.dp)),
                    placeholder = { Text("Buscar conductor") }
                )
                Button(
                    onClick = {
                        viewModel.getDriverList()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF1F504)
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Buscar")
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            state.message?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                val filteredDrivers = state.data?.filter { driver ->
                    driver.name.contains(searchQuery, ignoreCase = true) ||
                            driver.dni.contains(searchQuery, ignoreCase = true)
                } ?: emptyList()

                items(filteredDrivers.size) { index ->
                    val driver = filteredDrivers[index]
                    DriverItem(driver = driver)
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("register_driver?token=${Constants.TOKEN}") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFF1F504)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun DriverItem(driver: DriverDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A3A3A),
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Nombre: ${driver.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = "DNI: ${driver.dni}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "Licencia: ${driver.license}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "Número de contacto: ${driver.contactNumber}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}