package com.cargoexpress.app.core.presentation.driver.driverList


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import com.cargoexpress.app.core.common.Constants

@Composable
fun DriverListScreen(viewModel: DriverListViewModel = viewModel(), navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var filterByName by remember { mutableStateOf(true) }
    var sortAscending by remember { mutableStateOf(true) }

    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.getDriverList()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Título
            Text(
                text = "Mis Conductores",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar conductor") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Filtros y Ordenamiento
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filtro por tipo
                FilterChip(
                    selected = filterByName,
                    onClick = { filterByName = true },
                    label = { Text("Nombre") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                FilterChip(
                    selected = !filterByName,
                    onClick = { filterByName = false },
                    label = { Text("DNI") },
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Botón de ordenamiento
                FilterChip(
                    selected = true,
                    onClick = { sortAscending = !sortAscending },
                    label = {
                        Text(if (sortAscending) "↑ A-Z" else "↓ Z-A")
                    },
                    leadingIcon = { Icon(if (sortAscending) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFFFFEB3B)
                )
            }

            if (state.message.isNotBlank()) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredDrivers = state.data?.filter { driver ->
                    if (filterByName) {
                        driver.name.contains(searchQuery, ignoreCase = true)
                    } else {
                        driver.dni.contains(searchQuery, ignoreCase = true)
                    }
                } ?: emptyList()

                val sortedDrivers = if (filterByName) {
                    if (sortAscending) {
                        filteredDrivers.sortedBy { it.name }
                    } else {
                        filteredDrivers.sortedByDescending { it.name }
                    }
                } else {
                    filteredDrivers
                }

                if (sortedDrivers.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron conductores",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    items(sortedDrivers.size) { index ->
                        val driver = sortedDrivers[index]
                        DriverItem(driver = driver)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("register_driver?token=${Constants.TOKEN}") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFFFEB3B)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar conductor", tint = Color.Black)
        }
    }
}

@Composable
fun DriverItem(driver: DriverDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nombre con icono
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFFFFEB3B),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = driver.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Separador
            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // DNI con icono
            DriverInfoItem(
                icon = Icons.Filled.Info,
                label = "DNI",
                value = driver.dni
            )

            // Licencia con icono
            DriverInfoItem(
                icon = Icons.Filled.TimeToLeave,
                label = "Licencia",
                value = driver.license
            )

            // Contacto con icono
            DriverInfoItem(
                icon = Icons.Filled.Phone,
                label = "Contacto",
                value = driver.contactNumber
            )
        }
    }
}

@Composable
fun DriverInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(70.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}