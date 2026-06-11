package com.cargoexpress.app.core.presentation.driver.driverList

import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cargoexpress.app.core.domain.Driver

@Composable
fun DriverListScreen(viewModel: DriverListViewModel, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var filterByName by remember { mutableStateOf(true) }
    var sortAscending by remember { mutableStateOf(true) }

    val state by viewModel.state

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.destination?.route == "drivers") {
            viewModel.getDriverList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Mis Conductores",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                FilterChip(
                    selected = true,
                    onClick = { sortAscending = !sortAscending },
                    label = { Text(if (sortAscending) "↑ A-Z" else "↓ Z-A") },
                    leadingIcon = {
                        Icon(
                            if (sortAscending) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredDrivers = state.data?.filter { driver ->
                    if (filterByName) driver.name.contains(searchQuery, ignoreCase = true)
                    else driver.dni.contains(searchQuery, ignoreCase = true)
                } ?: emptyList()

                val sortedDrivers = if (filterByName) {
                    if (sortAscending) filteredDrivers.sortedBy { it.name }
                    else filteredDrivers.sortedByDescending { it.name }
                } else {
                    filteredDrivers
                }

                if (sortedDrivers.isEmpty() && !state.isLoading) {
                    item {
                        Text(
                            text = if (state.message.isNotEmpty()) state.message else "No se encontraron conductores",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(sortedDrivers.size) { index ->
                        val driver = sortedDrivers[index]
                        DriverItem(
                            driver = driver,
                            onEditClick = { navController.navigate("edit_driver/${driver.id}") }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("register_driver") },
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
fun DriverItem(driver: Driver, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color(0xFFF9A825),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = driver.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                DriverStateBadge(driver.state)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar conductor",
                        tint = Color(0xFFF9A825)
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            DriverInfoItem(icon = Icons.Filled.Info, label = "DNI", value = driver.dni)
            DriverInfoItem(icon = Icons.Filled.TimeToLeave, label = "Licencia", value = driver.license)
            DriverInfoItem(icon = Icons.Filled.Phone, label = "Contacto", value = driver.contactNumber)
        }
    }
}

@Composable
fun DriverStateBadge(state: String) {
    val (bgColor, textColor) = when (state) {
        "AVAILABLE" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "UNAVAILABLE" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        else -> Color(0xFFEEEEEE) to Color(0xFF616161)
    }
    val displayText = when (state) {
        "AVAILABLE" -> "Disponible"
        "UNAVAILABLE" -> "No disponible"
        else -> "Inactivo"
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bgColor) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
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
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
