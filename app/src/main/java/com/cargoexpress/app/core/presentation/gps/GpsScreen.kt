package com.cargoexpress.app.core.presentation.gps

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import android.view.View
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.OnMapReadyCallback

@Composable
fun GpsScreen(
    tripId: Int,
    tripRepository: TripRepository,
    navController: NavController,
    ongoingTripRepository: OngoingTripRepository
) {
    val viewModel: GpsViewModel = viewModel(factory = GpsViewModelFactory(ongoingTripRepository))
    val uiState by viewModel.uiState.collectAsState()
    val ongoingTrips by viewModel.ongoingTrips.collectAsState()
    val simulatedLat by viewModel.simulatedLat.collectAsState()
    val simulatedLng by viewModel.simulatedLng.collectAsState()
    val simulatedSpeed by viewModel.simulatedSpeed.collectAsState()

    val ongoingTrip = viewModel.getOngoingTripById(tripId)
    val hasOngoingTrip = ongoingTrip != null
    val isEntrepreneur = Constants.USER_ROLE == "ENTREPRENEUR"

    val mapView = rememberMapViewWithLifecycle()
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var vehicleMarker by remember { mutableStateOf<Marker?>(null) }
    var routePolyline by remember { mutableStateOf<Polyline?>(null) }
    var simulationStarted by remember { mutableStateOf(false) }
    var mapInitialized by remember { mutableStateOf(false) }

    val destination = LatLng(
        GpsViewModel.DESTINATION_LAT.toDouble(),
        GpsViewModel.DESTINATION_LNG.toDouble()
    )

    val currentLat = if (simulatedLat != 0f) simulatedLat else ongoingTrip?.latitude ?: 0f
    val currentLng = if (simulatedLng != 0f) simulatedLng else ongoingTrip?.longitude ?: 0f
    val currentPosition = LatLng(currentLat.toDouble(), currentLng.toDouble())
    val distanceMeters = if (currentLat != 0f || currentLng != 0f)
        SphericalUtil.computeDistanceBetween(currentPosition, destination) else 0.0
    val displaySpeed = if (simulatedSpeed != 0) simulatedSpeed else ongoingTrip?.speed ?: 0

    LaunchedEffect(ongoingTrips) {
        val trip = viewModel.getOngoingTripById(tripId)
        if (trip != null && !simulationStarted) {
            simulationStarted = true
            viewModel.startSimulation(tripId)
        }
    }

    LaunchedEffect(simulatedLat, simulatedLng, googleMap) {
        val map = googleMap ?: return@LaunchedEffect
        if (simulatedLat == 0f && simulatedLng == 0f) return@LaunchedEffect

        val newPos = LatLng(simulatedLat.toDouble(), simulatedLng.toDouble())
        if (vehicleMarker == null) {
            vehicleMarker = map.addMarker(
                MarkerOptions()
                    .position(newPos)
                    .title("Vehículo")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 13f))
        } else {
            vehicleMarker?.position = newPos
        }
        routePolyline?.remove()
        routePolyline = map.addPolyline(
            PolylineOptions()
                .add(newPos, destination)
                .width(7f)
                .color(android.graphics.Color.rgb(66, 133, 244))
                .geodesic(true)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    onCreate(Bundle())
                    getMapAsync { map ->
                        googleMap = map
                        map.uiSettings.isZoomControlsEnabled = true
                        map.uiSettings.isMapToolbarEnabled = false

                        map.addMarker(MarkerOptions().position(destination).title("Destino – Callao"))

                        val initLat = ongoingTrip?.latitude ?: 0f
                        val initLng = ongoingTrip?.longitude ?: 0f

                        if (initLat != 0f || initLng != 0f) {
                            val initPos = LatLng(initLat.toDouble(), initLng.toDouble())
                            vehicleMarker = map.addMarker(
                                MarkerOptions()
                                    .position(initPos)
                                    .title("Vehículo")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            )
                            routePolyline = map.addPolyline(
                                PolylineOptions()
                                    .add(initPos, destination)
                                    .width(7f)
                                    .color(android.graphics.Color.rgb(66, 133, 244))
                                    .geodesic(true)
                            )
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(initPos, 13f))
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 12f))
                        }

                        mapInitialized = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { navController.navigate("trips") },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver a viajes",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (hasOngoingTrip) {
                FilledIconButton(
                    onClick = { navController.navigate("alert/$tripId") },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Ver alertas",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Bottom info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Viaje #$tripId",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (hasOngoingTrip) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "EN PROGRESO",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (hasOngoingTrip && (currentLat != 0f || currentLng != 0f)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TripInfoItem(label = "Latitud", value = "%.4f°".format(currentLat))
                        TripInfoItem(label = "Longitud", value = "%.4f°".format(currentLng))
                        TripInfoItem(label = "Velocidad", value = "${displaySpeed} km/h")
                        TripInfoItem(
                            label = "Distancia",
                            value = if (distanceMeters >= 1000) "${"%.1f".format(distanceMeters / 1000)} km"
                            else "${distanceMeters.toInt()} m"
                        )
                    }
                } else if (!hasOngoingTrip) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEntrepreneur) "No hay viaje activo. Inicia el viaje para rastrear."
                        else "Sin viaje activo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (!hasOngoingTrip && isEntrepreneur) {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (uiState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else {
                        Button(
                            onClick = { viewModel.createOngoingTrip(tripId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Iniciar Viaje")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return mapView
}
