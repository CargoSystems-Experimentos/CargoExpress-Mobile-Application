package com.cargoexpress.app.core.presentation.trip.detailsTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository

class TripDetailViewModelFactory(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripDetailViewModel(
            tripRepository,
            expenseRepository,
            driverRepository,
            vehicleRepository,
            clientRepository
        ) as T
    }
}