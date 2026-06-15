package com.cargoexpress.app.core.common

sealed class Routes(val routes: String) {
    data object VehicleList: Routes("VehicleList")
    data object Login: Routes("Login")
    data object Register: Routes("Register")
    data object DriverList: Routes("DriverList")
    data object TripList: Routes("TripList")
    data object Profile: Routes("Profile")
    data object TermsAndConditions: Routes("TermsAndConditions")
    data object Home: Routes("home")
    data object Fleet: Routes("fleet")
    data object History: Routes("history")
}