package com.cargoexpress.app.core.presentation.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.domain.Alert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertViewModel(
    private val alertRepository: AlertRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState<Alert>())
    val uiState: StateFlow<UIState<Alert>> = _uiState

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess

    private val _tripName = MutableStateFlow("")
    val tripName: StateFlow<String> = _tripName

    private val _tripState = MutableStateFlow("")
    val tripState: StateFlow<String> = _tripState

    fun loadTripData(tripId: Int) {
        viewModelScope.launch {
            val result = tripRepository.getTripById(tripId)
            if (result is Resource.Success) {
                _tripName.value = result.data?.name ?: "Viaje #$tripId"
                _tripState.value = result.data?.state ?: ""
            } else {
                _tripName.value = "Viaje #$tripId"
            }
        }
    }

    fun loadAlerts(tripId: Int) {
        viewModelScope.launch {
            val result = alertRepository.getAlertsByTripId(Constants.TOKEN, tripId)
            if (result is Resource.Success) {
                _alerts.value = result.data ?: emptyList()
            } else {
                _uiState.value = UIState(isLoading = false, message = result.message ?: "")
            }
        }
    }

    fun createAlert(tripId: Int, title: String, type: String, description: String) {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val now = sdf.format(Date())

            val alert = Alert(
                id = 0,
                title = title,
                type = type,
                description = description,
                date = now,
                tripId = tripId
            )

            val result = alertRepository.createAlert(alert)
            if (result is Resource.Success) {
                _createSuccess.value = true
                loadAlerts(tripId)
                _uiState.value = UIState(isLoading = false)
            } else {
                _uiState.value = UIState(isLoading = false, message = result.message ?: "Error al crear alerta")
            }
        }
    }

    fun resetCreateSuccess() {
        _createSuccess.value = false
    }
}
