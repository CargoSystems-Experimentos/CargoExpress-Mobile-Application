package com.cargoexpress.app.core.presentation.trip.editTrip

import android.os.Build
import androidx.annotation.RequiresApi
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
    var weight: Int = 0
    var loadLocation: String = ""
    var loadDate: String = ""
    var unloadLocation: String = ""
    var unloadDate: String = ""
    var driverId: Int = 0
    var vehicleId: Int = 0
    var clientId: Int = 0
    var evidenceImg: String = ""

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
                evidenceImg = trip.evidenceImg

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTrip(onResult: (Resource<Trip>) -> Unit) {
        viewModelScope.launch {
            val trip = Trip(
                id = _uiState.value.trip?.id ?: 0,
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
                entrepreneurId = _uiState.value.trip?.entrepreneurId ?: 0,
                evidenceImg = evidenceImg
            )
            val result = tripRepository.updateTrip(trip)
            onResult(result)
        }
    }

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
