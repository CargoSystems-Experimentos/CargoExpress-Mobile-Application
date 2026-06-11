package com.cargoexpress.app.core.presentation.gps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
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
    private val tripRepository: TripRepository
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

    private val _isFinalized = MutableStateFlow(false)
    val isFinalized: StateFlow<Boolean> = _isFinalized

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

    fun getOngoingTripById(tripId: Int): OngoingTrip? =
        _ongoingTrips.value.find { it.tripId == tripId }

    fun createOngoingTrip(tripId: Int) {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
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
                tripRepository.updateTripState(tripId, "EN PROGRESO")
                loadOngoingTrips()
            } else {
                _uiState.value = UIState(isLoading = false, message = result.message ?: "Error al iniciar viaje")
            }
        }
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
                    finalizeTrip()
                    break
                }
            }
        }
    }

    private fun finalizeTrip() {
        viewModelScope.launch {
            _isFinalized.value = true
            loadOngoingTrips()
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}