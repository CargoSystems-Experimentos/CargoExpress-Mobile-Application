package com.cargoexpress.app.core.presentation.driver.driverList

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.domain.Driver
import com.cargoexpress.app.core.presentation.common.ConfirmationModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverListScreen(viewModel: DriverListViewModel, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var appliedQuery by remember { mutableStateOf("") }
    var sortAscending by remember { mutableStateOf(true) }
    var selectedState by remember { mutableStateOf("AVAILABLE") }

    var pendingDriver by remember { mutableStateOf<Driver?>(null) }
    var pendingNewState by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showResultModal by remember { mutableStateOf(false) }
    var resultSuccess by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }

    val stateOptions = listOf("AVAILABLE", "UNAVAILABLE", "INACTIVE")
    val stateLabels = mapOf("AVAILABLE" to "DISPONIBLE", "UNAVAILABLE" to "NO DISPONIBLE", "INACTIVE" to "INACTIVO")

    val state by viewModel.state

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.destination?.route == "drivers") {
            viewModel.getDriverList()
        }
    }

    if (showConfirmDialog && pendingDriver != null) {
        val isDeactivating = pendingNewState == "INACTIVE"
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                pendingDriver = null
            },
            title = { Text(if (isDeactivating) "Desactivar conductor" else "Restaurar conductor") },
            text = {
                Text(
                    if (isDeactivating)
                        "¿Estás seguro de que deseas desactivar a \"${pendingDriver?.name}\"? Podrás restaurarlo más adelante."
                    else
                        "¿Estás seguro de que deseas restaurar a \"${pendingDriver?.name}\"? Quedará disponible nuevamente."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val driver = pendingDriver ?: return@Button
                        showConfirmDialog = false
                        viewModel.updateDriverState(driver.id, pendingNewState) { result ->
                            resultSuccess = result is Resource.Success
                            resultMessage = if (result is Resource.Success)
                                if (isDeactivating) "Conductor desactivado correctamente"
                                else "Conductor restaurado correctamente"
                            else
                                (result as? Resource.Error)?.message ?: "Error al actualizar el estado"
                            showResultModal = true
                        }
                        pendingDriver = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDeactivating) Color(0xFFE65100) else Color(0xFF2E7D32)
                    )
                ) { Text(if (isDeactivating) "Desactivar" else "Restaurar", color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmDialog = false
                    pendingDriver = null
                }) { Text("Cancelar") }
            }
        )
    }

    if (showResultModal) {
        ConfirmationModal(
            isSuccess = resultSuccess,
            message = resultMessage,
            onConfirm = {
                showResultModal = false
                viewModel.getDriverList()
            },
            onDismiss = { showResultModal = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "MIS CONDUCTORES",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar conductor") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { appliedQuery = searchQuery },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFFEB3B))
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Black)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = true,
                    onClick = { sortAscending = !sortAscending },
                    label = { Text((if (sortAscending) "↑ A-Z" else "↓ Z-A"), style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp) ) },
                    leadingIcon = {
                        Icon(
                            if (sortAscending) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFEB3B)
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                stateOptions.forEach { s ->
                    FilterChip(
                        selected = selectedState == s,
                        onClick = { selectedState = s },
                        label = { Text((stateLabels[s] ?: s), style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp) ) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFEB3B)
                        )
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filtered = state.data?.filter { driver ->
                    val nameMatch = appliedQuery.isBlank() || driver.name.contains(appliedQuery, ignoreCase = true)
                    driver.state == selectedState && nameMatch
                } ?: emptyList()

                val sorted = if (sortAscending) filtered.sortedBy { it.name.lowercase() }
                             else filtered.sortedByDescending { it.name.lowercase() }

                if (sorted.isEmpty() && !state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "No se encontraron conductores\nEsto está muy solo",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(sorted.size) { index ->
                        val driver = sorted[index]
                        DriverItem(
                            driver = driver,
                            onEditClick = if (driver.state != "INACTIVE") {
                                { navController.navigate("edit_driver/${driver.id}") }
                            } else null,
                            onStateChangeClick = {
                                pendingDriver = driver
                                pendingNewState = if (driver.state == "INACTIVE") "AVAILABLE" else "INACTIVE"
                                showConfirmDialog = true
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("register_driver") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = Color(0xFFFFEB3B)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar conductor", tint = Color.Black)
        }
    }
}

@Composable
fun DriverItem(
    driver: Driver,
    onEditClick: (() -> Unit)?,
    onStateChangeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp)),
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
                Spacer(modifier = Modifier.width(4.dp))
                if (onEditClick != null) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar conductor", tint = Color(0xFFF9A825))
                    }
                }
                IconButton(onClick = onStateChangeClick, modifier = Modifier.size(36.dp)) {
                    if (driver.state == "INACTIVE") {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Restaurar conductor",
                            tint = Color(0xFF2E7D32)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Desactivar conductor",
                            tint = Color(0xFFE65100)
                        )
                    }
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
        "AVAILABLE" -> "DISPONIBLE"
        "UNAVAILABLE" -> "NO DISPONIBLE"
        else -> "INACTIVO"
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bgColor) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall,
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
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
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
