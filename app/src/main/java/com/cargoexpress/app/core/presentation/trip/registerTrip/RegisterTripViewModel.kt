package com.cargoexpress.app.core.presentation.trip.registerTrip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource
import com.cargoexpress.app.core.data.remote.driver.DriverDto
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterTripViewModel(
    private val tripRepository: TripRepository,
) : ViewModel() {
    var tripName: String = ""
    var cargoType: String = ""
    var weight: Int = 0
    var loadLocation: String = ""
    var loadDate: String = ""
    var unloadLocation: String = ""
    var unloadDate: String = ""
    var driverId: Int = 0
    var vehicleId: Int = 0
    var clientId: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerTrip(onResult: (Resource<Trip>) -> Unit) {
        viewModelScope.launch {
            val trip = Trip(
                id = 0,
                tripName = tripName,
                cargoType = cargoType,
                weight = weight,
                loadLocation = loadLocation,
                loadDate = loadDate,
                unloadLocation = unloadLocation,
                unloadDate = unloadDate,
                driverId = driverId,
                vehicleId = vehicleId,
                clientId = clientId,
                entrepreneurId = Constants.ENTREPRENEUR_ID
            )
            val result = tripRepository.addTrip(trip)
            onResult(result)
        }
    }
}