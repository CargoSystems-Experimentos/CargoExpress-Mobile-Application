package com.cargoexpress.app.core.presentation.alert

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.data.repository.TripRepository

@Composable
fun AlertScreen(
    tripId: Int,
    tripRepository: TripRepository,
    navController: NavController,
    alertRepository: AlertRepository
) {
    val viewModel: AlertViewModel = viewModel(factory = AlertViewModelFactory(alertRepository))
    val alerts = viewModel.alerts.collectAsState().value
    val alertInProgress = viewModel.getAlertById(tripId)

    LaunchedEffect(Unit) {
        viewModel.loadAlerts()
    }

    LaunchedEffect(alerts) {
        println("Alerts: $alerts")
        println(alertInProgress)
    }

    Text(text = "Hello World")
}