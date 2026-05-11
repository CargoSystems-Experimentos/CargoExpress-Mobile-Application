package com.cargoexpress.app.core.presentation.trip.detailsTrip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
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
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.domain.Trip
import java.text.SimpleDateFormat
import java.util.*
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.data.repository.ClientRepository
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Int,
    navController: NavController,
    tripRepository: TripRepository,
    expenseRepository: ExpenseRepository,
    driverRepository: DriverRepository,
    vehicleRepository: VehicleRepository,
    clientRepository: ClientRepository
) {
    // Si tienes un ViewModel factory diferente, ajusta la creación aquí
    val factory = TripDetailViewModelFactory(
        tripRepository,
        expenseRepository,
        driverRepository,
        vehicleRepository,
        clientRepository
    )
    val viewModel: TripDetailViewModel = viewModel(factory = factory)

    val trip by viewModel.trip.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    var showDetails by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    LaunchedEffect(tripId) {
        viewModel.loadTripDetails(tripId)
        viewModel.loadExpensesByTripId(tripId)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        trip?.let { t ->
            // Header: nombre + tipo + botones de acción
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Viaje",
                            tint = Color(0xFFFFEB3B),
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = t.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = t.type.ifBlank { "-" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            // Botones rápidos: detalles / gastos
            Column(horizontalAlignment = Alignment.End) {
                Row {
                    Button(
                        onClick = { showDetails = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Detalles", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Detalles")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showDetails = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "Gastos", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Gastos")
                    }
                }
            }

            // Contenido: alterna entre Detalles y Gastos
            if (showDetails) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Aquí mostramos iconos + título + contenido (estilo profile)
                        InfoItem(icon = Icons.Default.Scale, title = "Peso", content = "${t.weight} kg")
                        InfoItem(icon = Icons.Default.LocationOn, title = "Origen", content = t.loadLocation)
                        InfoItem(icon = Icons.Default.DateRange, title = "Fecha carga", content = formatDateTimeReadable(t.loadDate))
                        InfoItem(icon = Icons.Default.Place, title = "Destino", content = t.unloadLocation)
                        InfoItem(icon = Icons.Default.DateRange, title = "Fecha descarga", content = formatDateTimeReadable(t.unloadDate))

                        // Para mostrar nombres en lugar de IDs: el ViewModel debería exponer driverName/vehicleModel/clientName.
                        // Si no lo hace aún, mostramos fallback con ID.
                        val driverNameState by viewModel.driverName.collectAsState()
                        val vehicleModelState by viewModel.vehicleModel.collectAsState()
                        val clientNameState by viewModel.clientName.collectAsState()

                        InfoItem(icon = Icons.Default.Person, title = "Conductor", content = driverNameState.ifBlank { "ID: ${t.driverId}" })
                        InfoItem(icon = Icons.Default.DirectionsCar, title = "Vehículo", content = vehicleModelState.ifBlank { "ID: ${t.vehicleId}" })
                        InfoItem(icon = Icons.Default.Person, title = "Cliente", content = clientNameState.ifBlank { "ID: ${t.clientId}" })
                    }
                }
            } else {
                // Sección de Gastos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (expenses.isNullOrEmpty()) {
                            Text("No se han registrado gastos para este viaje.", style = MaterialTheme.typography.bodyMedium)
                            Button(onClick = { navController.navigate("register_expense/${t.id}") }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Agregar gasto")
                            }
                        } else {
                            // Lista de gastos
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(count = expenses.size) { index ->
                                    ExpenseCard(expenses[index])
                                }
                            }
                        }
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("Cargando detalles...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(text = content, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Gasto #${expense.id}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(6.dp))
            Text("Combustible: ${expense.fuelAmount} USD", style = MaterialTheme.typography.bodyMedium)
            Text("Viáticos: ${expense.viaticsAmount} USD", style = MaterialTheme.typography.bodyMedium)
            Text("Peajes: ${expense.tollsAmount} USD", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * Reusa el parse robusto de fechas pero con SimpleDateFormat (compatible con minSDK < 26).
 */
private fun formatDateTimeReadable(dateTime: String): String {
    if (dateTime.isBlank()) return "-"

    val inputPatterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )

    for (pattern in inputPatterns) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.getDefault())
            val parsed = parser.parse(dateTime)
            if (parsed != null) {
                val output = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                return output.format(parsed)
            }
        } catch (_: Exception) {
            // seguir con siguiente patrón
        }
    }

    return dateTime
}