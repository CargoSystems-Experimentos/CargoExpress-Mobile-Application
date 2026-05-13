package com.cargoexpress.app.core.presentation.trip.editTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository

class TripEditViewModelFactory(
    private val tripRepository: TripRepository,
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripEditViewModel(tripRepository, driverRepository, vehicleRepository, clientRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
