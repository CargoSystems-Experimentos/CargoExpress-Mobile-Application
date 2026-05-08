package com.cargoexpress.app.core.presentation.trip
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Trip
import pe.edu.upc.appturismo.common.Constants

@Composable
fun TripManagementScreen(
    tripRepository: TripRepository,
    ongoingTripRepository: OngoingTripRepository,
    navController: NavController
) {
    val factory = remember { TripManagementViewModelFactory(tripRepository, ongoingTripRepository) }
    val viewModel: TripManagementViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ID") }

    var isDescending by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadOngoingTrips(Constants.TOKEN)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.updateSearchQuery(searchQuery, selectedFilter)
                    },
                    onSearchClick = {
                        viewModel.updateSearchQuery(searchQuery, selectedFilter)
                    }
                )

                Button(
                    onClick = {
                        viewModel.updateSearchQuery(searchQuery, selectedFilter)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Buscar")
                }
            }
            FilterOptions(
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it },
                isDescending = isDescending,
                onOrderChange = { isDescending = !isDescending }
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                uiState.message.isNotBlank() -> {
                    Text(
                        text = uiState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else -> {
                    TripList(trips = uiState.data ?: emptyList(), navController = navController, isDescending = isDescending, viewModel = viewModel)
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("register_trip?token=${Constants.TOKEN}") },
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
fun FilterOptions(selectedFilter: String, onFilterChange: (String) -> Unit, isDescending: Boolean, onOrderChange: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Nombre", "Fecha").forEach { filter ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChange(filter) }
                    )
                    Text(text = filter)
                }
            }
        }
        IconButton(onClick = onOrderChange) {
            Icon(
                imageVector = if (isDescending) Icons.Default.FilterList else Icons.Default.FilterListOff,
                contentDescription = if (isDescending) "Orden Descendente" else "Orden Ascendente"
            )
        }
    }
}

@Composable
fun TripList(trips: List<Trip>, navController: NavController, isDescending: Boolean, viewModel: TripManagementViewModel) {
    val sortedTrips = if (isDescending) {
        trips.sortedByDescending { it.id }
    } else {
        trips.sortedBy { it.id }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sortedTrips) { trip ->
            TripCard(trip = trip, navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun TripCard(trip: Trip, navController: NavController, viewModel: TripManagementViewModel) {
    val ongoingTrip = viewModel.getOngoingTripById(trip.id)
    val isButtonEnabled = ongoingTrip != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("trip_details/${trip.id}") },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    text = "Viaje ${trip.tripName} #${trip.id}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF999900)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = AnnotatedString.Builder().apply {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("FECHA DE CARGA: ")
                        }
                        append(trip.loadDate)
                    }.toAnnotatedString()
                )
                Text(
                    text = AnnotatedString.Builder().apply {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("FECHA DE DESCARGA: ")
                        }
                        append(trip.unloadDate)
                    }.toAnnotatedString()
                )
            }
            Column {
                IconButton(
                    onClick = { navController.navigate("gps/${trip.id}") },
                    enabled = isButtonEnabled
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "GPS")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
