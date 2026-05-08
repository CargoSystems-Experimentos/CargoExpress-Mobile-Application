package com.cargoexpress.app.core.presentation.gps

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun GpsScreen(
    tripId: Int,
    tripRepository: TripRepository,
    navController: NavController,
    ongoingTripRepository: OngoingTripRepository
) {
    val viewModel: GpsViewModel = viewModel(factory = GpsViewModelFactory(ongoingTripRepository))
    val ongoingTrips = viewModel.ongoingTrips.collectAsState().value
    val tripInProgress = viewModel.getOngoingTripById(tripId)

    LaunchedEffect(Unit) {
        viewModel.loadOngoingTrips()
    }

    LaunchedEffect(ongoingTrips) {
        println("Ongoing Trips: $ongoingTrips")
        println(tripInProgress)
    }

    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()

    val latitude: Double? = tripInProgress?.latitude?.toDouble()
    val longitude: Double? = tripInProgress?.longitude?.toDouble()

    var distance by remember { mutableStateOf(0.0) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView({ mapView }) { mapView ->
            mapView.getMapAsync { googleMap ->
                if (latitude != null && longitude != null) {
                    val location = LatLng(latitude, longitude)
                    val callaoLocation = LatLng(-12.0613, -77.1528)
                    val markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)

                    val vehicleMarker = googleMap.addMarker(MarkerOptions().position(location).title("Vehiculo").icon(markerIcon))
                    val callaoMarker = googleMap.addMarker(MarkerOptions().position(callaoLocation).title("Destino"))

                    vehicleMarker?.showInfoWindow()
                    callaoMarker?.showInfoWindow()

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

                    googleMap.addPolyline(
                        PolylineOptions()
                            .add(location, callaoLocation)
                            .width(5f)
                            .color(Color.RED)
                    )
                    distance = SphericalUtil.computeDistanceBetween(location, callaoLocation)
                    println("Distance: $distance meters")

                } else {
                    println("Latitude or Longitude is null")
                }
            }
        }
        // Card with trip information
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Información del Viaje", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(text = "Latitud:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${latitude ?: "${tripInProgress?.latitude}"}", fontSize = 16.sp)
                    }
                    Row {
                        Text(text = "Longitud:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${longitude ?: "${tripInProgress?.longitude}"}", fontSize = 16.sp)
                    }
                    Row {
                        Text(text = "Velocidad:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${tripInProgress?.speed} km/h", fontSize = 16.sp)
                    }
                    Row {
                        Text(text = "Distancia:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (distance >= 1000) {
                                "${(distance / 1000).toInt()} km"
                            } else {
                                "${distance.toInt()} m"
                            },
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
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