package com.cargoexpress.app.core.presentation.gps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.OngoingTrip
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

class GpsViewModel(
    private val ongoingTripRepository: OngoingTripRepository,
    private val tripRepository: TripRepository,
    private val vehicleRepository: VehicleRepository,
    private val driverRepository: DriverRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState<OngoingTrip>(isLoading = true))
    val uiState: StateFlow<UIState<OngoingTrip>> = _uiState

    private val _ongoingTrips = MutableStateFlow<List<OngoingTrip>>(emptyList())
    val ongoingTrips: StateFlow<List<OngoingTrip>> = _ongoingTrips

    private val _simulatedLat = MutableStateFlow(0f)
    val simulatedLat: StateFlow<Float> = _simulatedLat

    private val _simulatedLng = MutableStateFlow(0f)
    val simulatedLng: StateFlow<Float> = _simulatedLng

    private val _simulatedSpeed = MutableStateFlow(0)
    val simulatedSpeed: StateFlow<Int> = _simulatedSpeed

    private var simulationJob: Job? = null

    private val _tripName = MutableStateFlow("Viaje")
    val tripName: StateFlow<String> = _tripName

    private val _tripState = MutableStateFlow("")
    val tripState: StateFlow<String> = _tripState

    private var tripVehicleId: Int = -1
    private var tripDriverId: Int = -1

    data class ActionResult(val success: Boolean, val message: String)

    private val _actionResult = MutableStateFlow<ActionResult?>(null)
    val actionResult: StateFlow<ActionResult?> = _actionResult

    companion object {
        const val DESTINATION_LAT = -12.0613f
        const val DESTINATION_LNG = -77.1528f
    }

    init {
        loadOngoingTrips()
    }

    fun loadOngoingTrips() {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
            val result = ongoingTripRepository.getOngoingTrips(Constants.TOKEN)
            if (result is Resource.Success) {
                _ongoingTrips.value = result.data ?: emptyList()
                _uiState.value = UIState(isLoading = false)
            } else {
                _uiState.value = UIState(isLoading = false, message = result.message ?: "")
            }
        }
    }

    fun loadTripData(tripId: Int) {
        viewModelScope.launch {
            val result = tripRepository.getTripById(tripId)
            if (result is Resource.Success && result.data != null) {
                _tripName.value = result.data.name
                _tripState.value = result.data.state
                tripVehicleId = result.data.vehicleId
                tripDriverId = result.data.driverId
            }
        }
    }

    private fun releaseResources() {
        if (tripVehicleId > 0) {
            viewModelScope.launch { vehicleRepository.updateVehicleState(tripVehicleId, "AVAILABLE") }
        }
        if (tripDriverId > 0) {
            viewModelScope.launch { driverRepository.updateDriverState(tripDriverId, "AVAILABLE") }
        }
    }

    fun getOngoingTripById(tripId: Int): OngoingTrip? =
        _ongoingTrips.value.find { it.tripId == tripId }

    fun createOngoingTrip(tripId: Int) {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)

            val stateResult = tripRepository.updateTripState(tripId, "PROGRESS")
            if (stateResult !is Resource.Success) {
                _uiState.value = UIState(isLoading = false, message = stateResult.message ?: "Error al actualizar el estado del viaje")
                return@launch
            }
            _tripState.value = "PROGRESS"

            val lat = Random.nextDouble(-12.02, -11.95).toFloat()
            val lng = Random.nextDouble(-77.06, -77.00).toFloat()
            val speed = Random.nextInt(30, 100)
            val distance = Random.nextInt(5000, 30000)

            val newTrip = OngoingTrip(
                latitude = lat,
                longitude = lng,
                speed = speed,
                distance = distance,
                tripId = tripId
            )
            val result = ongoingTripRepository.createOngoingTrip(Constants.TOKEN, newTrip)
            if (result is Resource.Success) {
                loadOngoingTrips()
            } else {
                _uiState.value = UIState(isLoading = false, message = result.message ?: "Error al iniciar viaje")
            }
        }
    }

    fun finishTrip(tripId: Int) {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
            val result = tripRepository.updateTripState(tripId, "FINISHED")
            _uiState.value = UIState(isLoading = false)
            if (result is Resource.Success) {
                simulationJob?.cancel()
                _tripState.value = "FINISHED"
                releaseResources()
                _actionResult.value = ActionResult(success = true, message = "El viaje ha finalizado correctamente.")
            } else {
                _actionResult.value = ActionResult(success = false, message = result.message ?: "Error al finalizar el viaje")
            }
        }
    }

    fun cancelTrip(tripId: Int) {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
            val result = tripRepository.updateTripState(tripId, "CANCELED")
            _uiState.value = UIState(isLoading = false)
            if (result is Resource.Success) {
                simulationJob?.cancel()
                _tripState.value = "CANCELED"
                releaseResources()
                _actionResult.value = ActionResult(success = true, message = "El viaje ha sido cancelado.")
            } else {
                _actionResult.value = ActionResult(success = false, message = result.message ?: "Error al cancelar el viaje")
            }
        }
    }

    fun clearActionResult() {
        _actionResult.value = null
    }

    fun startSimulation(tripId: Int) {
        val trip = getOngoingTripById(tripId) ?: return
        _simulatedLat.value = trip.latitude
        _simulatedLng.value = trip.longitude
        _simulatedSpeed.value = trip.speed

        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (true) {
                delay(5000)
                val currentLat = _simulatedLat.value
                val currentLng = _simulatedLng.value

                val newLat = currentLat + (DESTINATION_LAT - currentLat) * 0.1f
                val newLng = currentLng + (DESTINATION_LNG - currentLng) * 0.1f

                _simulatedLat.value = newLat
                _simulatedLng.value = newLng
                _simulatedSpeed.value = Random.nextInt(20, 90)

                if (abs(newLat - DESTINATION_LAT) < 0.0005f && abs(newLng - DESTINATION_LNG) < 0.0005f) {
                    _simulatedLat.value = DESTINATION_LAT
                    _simulatedLng.value = DESTINATION_LNG
                    _simulatedSpeed.value = 0
                    break
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}
