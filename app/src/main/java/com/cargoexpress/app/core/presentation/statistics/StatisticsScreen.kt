package com.cargoexpress.app.core.presentation.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.domain.Trip
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun StatisticsScreen(tripRepository: TripRepository) {
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(tripRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val totalTrips = trips.size
    val origins = trips.groupBy { it.loadLocation }
    val destinations = trips.groupBy { it.unloadLocation }
    val topOrigin = origins.maxByOrNull { it.value.size }
    val topDestination = destinations.maxByOrNull { it.value.size }
    val recentTrips = trips.sortedByDescending { it.loadDate }.take(5)

    val totalFuel = expenses.sumOf { it.fuelAmount }
    val totalViatics = expenses.sumOf { it.viaticsAmount }
    val totalTolls = expenses.sumOf { it.tollsAmount }
    val totalExpenses = totalFuel + totalViatics + totalTolls

    val expensesByTrip: List<Pair<String, Double>> = expenses
        .groupBy { it.tripId }
        .map { (tripId, exps) ->
            val trip = trips.find { it.id == tripId }
            val tripName = trip?.name ?: "Viaje #$tripId"
            val total = exps.sumOf { it.fuelAmount + it.viaticsAmount + it.tollsAmount }
            tripName to total
        }
        .sortedByDescending { it.second }
        .take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column{
            Text(
                text = "MIS ESTADISTICAS",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),

            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Tus numeros en un vistazo",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal, fontSize = 15.sp),
                color = Color.Gray
            )
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFEB3B))
                }
            }

            uiState.message.isNotBlank() -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatSummaryCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.LocalShipping,
                                iconBg = Color(0xFFFFF8E1),
                                iconTint = Color(0xFFF9A825),
                                label = "Total viajes",
                                value = totalTrips.toString()
                            )
                            StatSummaryCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Route,
                                iconBg = Color(0xFFE3F2FD),
                                iconTint = Color(0xFF1565C0),
                                label = "Rutas únicas",
                                value = origins.size.toString()
                            )
                        }
                    }

                    item {
                        StatSummaryCard(
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.AttachMoney,
                            iconBg = Color(0xFFE8F5E9),
                            iconTint = Color(0xFF2E7D32),
                            label = "Total gastos",
                            value = "$ ${"%,.2f".format(totalExpenses)}"
                        )
                    }

                    item {
                        RouteCard(
                            title = "Origen más frecuente",
                            icon = Icons.Default.LocationOn,
                            iconBg = Color(0xFFE8F5E9),
                            iconTint = Color(0xFF2E7D32),
                            location = topOrigin?.key ?: "-",
                            count = topOrigin?.value?.size ?: 0
                        )
                    }

                    item {
                        RouteCard(
                            title = "Destino más frecuente",
                            icon = Icons.Default.Place,
                            iconBg = Color(0xFFFCE4EC),
                            iconTint = Color(0xFFC62828),
                            location = topDestination?.key ?: "-",
                            count = topDestination?.value?.size ?: 0
                        )
                    }

                    if (totalExpenses > 0) {
                        item {
                            ExpenseCategoryBarChart(
                                fuelTotal = totalFuel,
                                viaticsTotal = totalViatics,
                                tollsTotal = totalTolls
                            )
                        }
                    }

                    if (expensesByTrip.isNotEmpty()) {
                        item {
                            ExpensePerTripBarChart(entries = expensesByTrip)
                        }
                    }

                    if (recentTrips.isNotEmpty()) {
                        item {
                            Text(
                                text = "Viajes recientes",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        items(recentTrips) { trip ->
                            RecentTripCard(trip = trip, expenses = expenses.filter { it.tripId == trip.id })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseCategoryBarChart(
    fuelTotal: Double,
    viaticsTotal: Double,
    tollsTotal: Double
) {
    val entries = listOf(
        Triple("Combustible", fuelTotal, Color(0xFFF9A825)),
        Triple("Viáticos", viaticsTotal, Color(0xFF1565C0)),
        Triple("Peajes", tollsTotal, Color(0xFF2E7D32))
    )
    val maxValue = entries.maxOf { it.second }.toFloat().coerceAtLeast(1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Gastos por categoría",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                entries.forEach { (label, value, color) ->
                    val fraction = value.toFloat() / maxValue
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "S/${"%,.0f".format(value)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height((120 * fraction).dp.coerceAtLeast(4.dp))
                                .background(color, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpensePerTripBarChart(entries: List<Pair<String, Double>>) {
    val maxValue = entries.maxOf { it.second }.toFloat().coerceAtLeast(1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Top gastos por viaje",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                entries.forEach { (label, value) ->
                    val fraction = value.toFloat() / maxValue
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "S/${"%,.0f".format(value)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height((120 * fraction).dp.coerceAtLeast(4.dp))
                                .background(Color(0xFF1565C0), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatSummaryCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RouteCard(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    location: String,
    count: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFF8E1)
            ) {
                Text(
                    text = "$count viajes",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFF9A825)
                )
            }
        }
    }
}

@Composable
private fun RecentTripCard(trip: Trip, expenses: List<Expense>) {
    val tripTotal = expenses.sumOf { it.fuelAmount + it.viaticsAmount + it.tollsAmount }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = Color(0xFFF9A825),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${trip.loadLocation} → ${trip.unloadLocation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (tripTotal > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Gastos: S/ ${"%,.2f".format(tripTotal)}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                    )
                }
            }
            Text(
                text = formatDate(trip.loadDate),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun formatDate(dateTime: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val parsed = input.parse(dateTime)
        if (parsed != null) output.format(parsed) else dateTime.take(10)
    } catch (e: Exception) {
        dateTime.take(10)
    }
}
