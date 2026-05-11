package com.cargoexpress.app.core.presentation.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.OngoingTrip
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState

class TripManagementViewModel(
    private val tripRepository: TripRepository,
    private val ongoingTripRepository: OngoingTripRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState<List<Trip>>(isLoading = true))
    val uiState: StateFlow<UIState<List<Trip>>> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var allTrips: List<Trip> = emptyList()
    private var allOngoingTrips: List<OngoingTrip> = emptyList()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
            val result = tripRepository.getTrips(Constants.TOKEN, Constants.ENTREPRENEUR_ID)
            _uiState.value = when (result) {
                is Resource.Success -> {
                    allTrips = result.data ?: emptyList()
                    UIState(data = allTrips, isLoading = false)
                }
                is Resource.Error -> UIState(isLoading = false, message = result.message ?: "Failed to load trips")
            }
        }
    }

    fun loadOngoingTrips(token: String) {
        viewModelScope.launch {
            val result = ongoingTripRepository.getOngoingTrips(token)
            if (result is Resource.Success) {
                allOngoingTrips = result.data ?: emptyList()
            } else {
                handleError(Exception(result.message))
            }
        }
    }
    fun getOngoingTripById(tripId: Int): OngoingTrip? {
        return allOngoingTrips.find { it.tripId == tripId }
    }

    fun updateSearchQuery(query: String, selectedFilter: String) {
        _searchQuery.value = query
        filterTrips(query, selectedFilter)
    }

    private fun filterTrips(query: String, selectedFilter: String) {
        val filteredTrips = allTrips.filter { trip ->
            when (selectedFilter) {
                "Nombre" -> trip.name.contains(query, ignoreCase = true)
                "Tipo" -> trip.type.contains(query, ignoreCase = true)
                "Fecha" -> trip.loadDate.contains(query, ignoreCase = true) ||
                        trip.unloadDate.contains(query, ignoreCase = true)
                else -> false
            }
        }

        _uiState.value = _uiState.value.copy(data = filteredTrips, isLoading = false)
    }

    fun handleError(exception: Exception) {
        val message = exception.message ?: "Unknown error"
        _uiState.value = UIState(isLoading = false, message = "Error: $message")
    }
}
