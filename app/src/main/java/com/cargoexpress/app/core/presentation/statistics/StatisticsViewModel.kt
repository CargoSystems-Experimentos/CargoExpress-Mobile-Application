package com.cargoexpress.app.core.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(private val tripRepository: TripRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState<List<Trip>>(isLoading = true))
    val uiState: StateFlow<UIState<List<Trip>>> = _uiState

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips

    init {
        loadTrips()
    }

    fun loadTrips() {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
            val result = tripRepository.getTripsByClientId(Constants.TOKEN, Constants.CLIENT_ID)
            if (result is Resource.Success) {
                _trips.value = result.data ?: emptyList()
                _uiState.value = UIState(isLoading = false, data = result.data)
            } else {
                _uiState.value = UIState(isLoading = false, message = result.message ?: "")
            }
        }
    }
}
