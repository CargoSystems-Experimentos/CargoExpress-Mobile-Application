package com.cargoexpress.app.core.presentation.gps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository

class GpsViewModelFactory(
    private val ongoingTripRepository: OngoingTripRepository,
    private val tripRepository: TripRepository,
    private val vehicleRepository: VehicleRepository,
    private val driverRepository: DriverRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GpsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GpsViewModel(ongoingTripRepository, tripRepository, vehicleRepository, driverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
