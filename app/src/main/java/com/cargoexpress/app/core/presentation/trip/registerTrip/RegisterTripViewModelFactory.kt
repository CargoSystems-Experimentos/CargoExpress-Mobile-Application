package com.cargoexpress.app.core.presentation.trip.registerTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository

class RegisterTripViewModelFactory(
    private val tripRepository: TripRepository,
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository,
    private val entrepreneurRepository: EntrepreneurRepository,
    private val clientRepository: ClientRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterTripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterTripViewModel(
                tripRepository = tripRepository,
                driverRepository = driverRepository,
                vehicleRepository = vehicleRepository,
                clientRepository = clientRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}