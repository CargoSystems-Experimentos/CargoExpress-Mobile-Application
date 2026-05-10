package com.cargoexpress.app.core.presentation.trip.registerTrip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource

class RegisterTripViewModel(
    private val tripRepository: TripRepository,
) : ViewModel() {
    var name: String = ""
    var type: String = ""
    var weight: Int = 0
    var loadLocation: String = ""
    var loadDate: String = ""
    var unloadLocation: String = ""
    var unloadDate: String = ""
    var driverId: Int = 0
    var vehicleId: Int = 0
    var clientId: Int = 0
    var evidenceImg: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerTrip(onResult: (Resource<Trip>) -> Unit) {
        viewModelScope.launch {
            val trip = Trip(
                id = 0,
                name = name,
                type = type,
                weight = weight,
                loadLocation = loadLocation,
                loadDate = loadDate,
                unloadLocation = unloadLocation,
                unloadDate = unloadDate,
                driverId = driverId,
                vehicleId = vehicleId,
                clientId = clientId,
                entrepreneurId = Constants.ENTREPRENEUR_ID,
                evidenceImg = evidenceImg
            )
            val result = tripRepository.addTrip(trip)
            onResult(result)
        }
    }
}