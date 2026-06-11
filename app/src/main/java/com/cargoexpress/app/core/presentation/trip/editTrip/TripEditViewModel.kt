package com.cargoexpress.app.core.presentation.trip.editTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.data.remote.user.ClientDto
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Driver
import com.cargoexpress.app.core.domain.Trip
import com.cargoexpress.app.core.domain.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TripEditViewModel(
    private val tripRepository: TripRepository,
    private val driverRepository: DriverRepository,
    private val vehicleRepository: VehicleRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    var name: String = ""
    var type: String = ""
    var weight: Double = 0.0
    var loadLocation: String = ""
    var loadDate: String = ""
    var unloadLocation: String = ""
    var unloadDate: String = ""
    var driverId: Int = 0
    var vehicleId: Int = 0
    var clientId: Int = 0

    private var originalDriverId: Int = 0
    private var originalVehicleId: Int = 0

    private val _uiState = MutableStateFlow(TripEditUiState())
    val uiState: StateFlow<TripEditUiState> = _uiState

    fun loadTrip(tripId: Int) {
        viewModelScope.launch {
            _uiState.value = TripEditUiState(isLoading = true)
            val resource = tripRepository.getTripById(tripId)
            if (resource is Resource.Success) {
                val trip = resource.data ?: return@launch
                name = trip.name
                type = trip.type
                weight = trip.weight
                loadLocation = trip.loadLocation
                loadDate = trip.loadDate
                unloadLocation = trip.unloadLocation
                unloadDate = trip.unloadDate
                driverId = trip.driverId
                vehicleId = trip.vehicleId
                clientId = trip.clientId
                originalDriverId = trip.driverId
                originalVehicleId = trip.vehicleId

                var preloadedDriverName = ""
                var preloadedVehicleModel = ""
                var preloadedClientDni = ""
                var preloadedClientName = ""

                try {
                    val drivers = driverRepository.getDrivers(Constants.TOKEN, Constants.ENTREPRENEUR_ID)
                    if (drivers is Resource.Success) {
                        preloadedDriverName = drivers.data?.find { it.id == trip.driverId }?.name ?: ""
                    }
                } catch (_: Exception) {}

                try {
                    val vehicles = vehicleRepository.getVehicleList(Constants.TOKEN, Constants.ENTREPRENEUR_ID)
                    if (vehicles is Resource.Success) {
                        preloadedVehicleModel = vehicles.data?.find { it.id == trip.vehicleId }?.model ?: ""
                    }
                } catch (_: Exception) {}

                try {
                    val clientResult = clientRepository.getClient(trip.clientId, Constants.TOKEN)
                    clientResult.getOrNull()?.let {
                        preloadedClientDni = it.dni
                        preloadedClientName = it.name
                    }
                } catch (_: Exception) {}

                _uiState.value = TripEditUiState(
                    trip = trip,
                    isLoading = false,
                    preloadedDriverName = preloadedDriverName,
                    preloadedVehicleModel = preloadedVehicleModel,
                    preloadedClientDni = preloadedClientDni,
                    preloadedClientName = preloadedClientName
                )
            } else {
                _uiState.value = TripEditUiState(isLoading = false)
            }
        }
    }

    fun updateTrip(onResult: (Resource<Trip>) -> Unit) {
        viewModelScope.launch {
            val currentTrip = _uiState.value.trip ?: return@launch
            val trip = buildCurrentTrip(currentTrip)
            val detailsResult = tripRepository.updateTripDetails(trip)
            if (detailsResult is Resource.Error) { onResult(detailsResult); return@launch }
            val scheduleResult = tripRepository.updateTripSchedule(trip)
            if (scheduleResult is Resource.Error) { onResult(scheduleResult); return@launch }
            if (driverId != originalDriverId && originalDriverId > 0) {
                driverRepository.updateDriverState(originalDriverId, "AVAILABLE")
                driverRepository.updateDriverState(driverId, "UNAVAILABLE")
            }
            if (vehicleId != originalVehicleId && originalVehicleId > 0) {
                vehicleRepository.updateVehicleState(originalVehicleId, "AVAILABLE")
                vehicleRepository.updateVehicleState(vehicleId, "UNAVAILABLE")
            }
            onResult(Resource.Success(data = trip))
        }
    }

    fun updateTripDetailsOnly(onResult: (Resource<Trip>) -> Unit) {
        viewModelScope.launch {
            val currentTrip = _uiState.value.trip ?: return@launch
            val trip = buildCurrentTrip(currentTrip)
            val result = tripRepository.updateTripDetails(trip)
            if (result is Resource.Error) { onResult(result); return@launch }
            if (driverId != originalDriverId && originalDriverId > 0) {
                driverRepository.updateDriverState(originalDriverId, "AVAILABLE")
                driverRepository.updateDriverState(driverId, "UNAVAILABLE")
                originalDriverId = driverId
            }
            if (vehicleId != originalVehicleId && originalVehicleId > 0) {
                vehicleRepository.updateVehicleState(originalVehicleId, "AVAILABLE")
                vehicleRepository.updateVehicleState(vehicleId, "UNAVAILABLE")
                originalVehicleId = vehicleId
            }
            onResult(Resource.Success(data = trip))
        }
    }

    fun updateTripScheduleOnly(onResult: (Resource<Trip>) -> Unit) {
        viewModelScope.launch {
            val currentTrip = _uiState.value.trip ?: return@launch
            val trip = buildCurrentTrip(currentTrip)
            onResult(tripRepository.updateTripSchedule(trip))
        }
    }

    private fun buildCurrentTrip(currentTrip: Trip) = Trip(
        id = currentTrip.id, name = name, state = currentTrip.state,
        type = type, weight = weight, loadLocation = loadLocation,
        loadDate = loadDate, unloadLocation = unloadLocation, unloadDate = unloadDate,
        driverId = driverId, vehicleId = vehicleId, clientId = clientId,
        entrepreneurId = currentTrip.entrepreneurId
    )

    suspend fun getDrivers(entrepreneurId: Int): Resource<List<Driver>> {
        return driverRepository.getDrivers(Constants.TOKEN, entrepreneurId)
    }

    suspend fun getVehicles(entrepreneurId: Int): Resource<List<Vehicle>> {
        return vehicleRepository.getVehicleList(Constants.TOKEN, entrepreneurId)
    }

    suspend fun validateClientDni(dni: String): Result<ClientDto> {
        return clientRepository.getClientByDni(dni, Constants.TOKEN)
    }
}

data class TripEditUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = false,
    val preloadedDriverName: String = "",
    val preloadedVehicleModel: String = "",
    val preloadedClientDni: String = "",
    val preloadedClientName: String = ""
)
