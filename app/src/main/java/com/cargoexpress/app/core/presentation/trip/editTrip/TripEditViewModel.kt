package com.cargoexpress.app.core.presentation.trip.editTrip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Trip
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TripEditViewModel(
    private val tripRepository: TripRepository,
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
            _uiState.value = _uiState.value.copy(isLoading = true)
            val resource = tripRepository.getTripById(tripId)
            if (resource is Resource.Success) {
                val trip = resource.data
                name = trip?.name ?: ""
                type = trip?.type ?: ""
                weight = trip?.weight ?: 0
                loadLocation = trip?.loadLocation ?: ""
                loadDate = trip?.loadDate ?: ""
                unloadLocation = trip?.unloadLocation ?: ""
                unloadDate = trip?.unloadDate ?: ""
                driverId = trip?.driverId ?: 0
                vehicleId = trip?.vehicleId ?: 0
                clientId = trip?.clientId ?: 0
                evidenceImg = trip?.evidenceImg ?: ""
                _uiState.value = _uiState.value.copy(trip = trip, isLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
                // Handle error case if needed
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
}

data class TripEditUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = false
)