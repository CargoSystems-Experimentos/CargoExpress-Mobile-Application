package com.cargoexpress.app.core.presentation.gps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.domain.OngoingTrip
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource
import pe.edu.upc.appturismo.common.UIState

class GpsViewModel(private val ongoingTripRepository: OngoingTripRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UIState<List<Trip>>(isLoading = true))
    val uiState: StateFlow<UIState<List<Trip>>> = _uiState

    private val _ongoingTrips = MutableStateFlow<List<OngoingTrip>>(emptyList())
    val ongoingTrips: StateFlow<List<OngoingTrip>> = _ongoingTrips

    init {
        loadOngoingTrips()
    }

    fun loadOngoingTrips() {
        viewModelScope.launch {
            val result = ongoingTripRepository.getOngoingTrips(Constants.TOKEN)
            if (result is Resource.Success) {
                _ongoingTrips.value = result.data ?: emptyList()
            } else {
                handleError(Exception(result.message))
            }
        }
    }

    fun getOngoingTripById(tripId: Int): OngoingTrip? {
        return _ongoingTrips.value.find { it.tripId == tripId }
    }

    private fun handleError(exception: Exception) {
        val message = exception.message ?: "Unknown error"
        _uiState.value = UIState(isLoading = false, message = "Error: $message")
    }
}

