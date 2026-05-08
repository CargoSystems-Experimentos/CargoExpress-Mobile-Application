package com.cargoexpress.app.core.common

object Screens {
    val registerScreen = Screen("registerScreen")
    val fleetScreen = Screen("fleetScreen")
    val historyScreen = Screen("historyScreen")
    val GPSScreen = Screen("GPSScreen")
    data class Screen(val route: String)
}