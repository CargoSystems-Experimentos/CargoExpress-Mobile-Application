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
import com.cargoexpress.app.core.data.remote.driver.toDriver
import com.cargoexpress.app.core.data.remote.vehicle.toVehicle
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

    private val _vehicleModel = MutableStateFlow("")
    val vehicleModel: StateFlow<String> = _vehicleModel

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
            when (val result = expenseRepository.getExpensesByTripId(Constants.TOKEN, tripId)) {
                is Resource.Success -> _expenses.value = result.data ?: emptyList()
                is Resource.Error -> _expenses.value = emptyList()
            }
        }
    }


    private fun loadRelatedNames(trip: Trip) {
        viewModelScope.launch {
            // Cargar conductor por ID
            try {
                val driverResult = driverRepository.getDrivers(Constants.TOKEN, trip.id)
                if (driverResult is Resource.Success) {
                    val driver = driverResult.data?.find { it.id == trip.driverId }
                    _driverName.value = driver?.name ?: ""
                }
            } catch (_: Exception) { }

            // Cargar vehículo por ID
            try {
                val vehicleResult = vehicleRepository.getVehicleList(Constants.TOKEN, trip.id)
                if (vehicleResult is Resource.Success) {
                    val vehicle = vehicleResult.data?.find { it.id == trip.vehicleId }
                    _vehicleModel.value = vehicle?.model ?: ""
                }
            } catch (_: Exception) { }

            // Cargar cliente por ID
            try {
                val clientResult = clientRepository.getClient(trip.clientId, Constants.TOKEN)
                if (clientResult.isSuccess) {
                    val clientDto = clientResult.getOrNull()
                    _clientName.value = clientDto?.name ?: ""
                }
            } catch (_: Exception) { }
        }
    }
}