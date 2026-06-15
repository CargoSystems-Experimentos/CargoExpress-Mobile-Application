package com.cargoexpress.app.core.presentation.trip.detailsTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Expense
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource

class TripDetailViewModel(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> get() = _trip

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> get() = _expenses

    private val _driverName = MutableStateFlow("")
    val driverName: StateFlow<String> = _driverName

    private val _vehicleName = MutableStateFlow("")
    val vehicleModel: StateFlow<String> = _vehicleName

    private val _clientName = MutableStateFlow("")
    val clientName: StateFlow<String> = _clientName

    fun loadTripDetails(tripId: Int) {
        viewModelScope.launch {
            when (val result = tripRepository.getTripById(tripId)) {
                is Resource.Success -> {
                    _trip.value = result.data
                    result.data?.let { loadRelatedNames(it) }
                }
                is Resource.Error -> _trip.value = null
            }
        }
    }

    fun loadExpensesByTripId(tripId: Int) {
        viewModelScope.launch {
            when (val result = tripRepository.getExpenseByTripId(tripId)) {
                is Resource.Success -> {
                    val expense = result.data
                    _expenses.value = if (expense != null) listOf(expense) else emptyList()
                }
                is Resource.Error -> _expenses.value = emptyList()
            }
        }
    }


    private fun loadRelatedNames(trip: Trip) {
        viewModelScope.launch {
            try {
                val driverResult = driverRepository.getDriverById(trip.driverId)
                if (driverResult is Resource.Success) {
                    _driverName.value = driverResult.data?.name ?: ""
                }
            } catch (_: Exception) { }

            try {
                val vehicleResult = vehicleRepository.getVehicleById(trip.vehicleId)
                if (vehicleResult is Resource.Success) {
                    _vehicleName.value = vehicleResult.data?.name ?: ""
                }
            } catch (_: Exception) { }

            try {
                val clientResult = clientRepository.getClient(trip.clientId, Constants.TOKEN)
                if (clientResult.isSuccess) {
                    _clientName.value = clientResult.getOrNull()?.name ?: ""
                }
            } catch (_: Exception) { }
        }
    }
}