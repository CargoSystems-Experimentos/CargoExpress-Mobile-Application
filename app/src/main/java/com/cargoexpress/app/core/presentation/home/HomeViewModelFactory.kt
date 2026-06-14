package com.cargoexpress.app.core.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.data.repository.AuditLogRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository

class HomeViewModelFactory(
    private val tripRepository: TripRepository,
    private val auditLogRepository: AuditLogRepository,
    private val vehicleRepository: VehicleRepository,
    private val driverRepository: DriverRepository,
    private val alertRepository: AlertRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(tripRepository, auditLogRepository, vehicleRepository, driverRepository, alertRepository) as T
}
