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

    init {
        loadAlerts()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            val result = alertRepository.getAlerts(Constants.TOKEN)
            if (result is Resource.Success) {
                _alerts.value = result.data ?: emptyList()
            } else {
                _uiState.value = UIState(isLoading = false, message = result.message ?: "")
            }
        }
    }

    var title: String = ""
    var description: String = ""
    fun createAlert(tripId: Int, title: String, description: String) {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val now = sdf.format(Date())

            val ongoingResult = tripRepository.getOngoingTripByTripId(tripId)

            if (ongoingResult is Resource.Success && ongoingResult.data != null) {
                val ongoingTripId = ongoingResult.data.id

                val alert = Alert(
                    id = 0,
                    title = title,
                    description = description,
                    date = now,
                    ongoingTripId = ongoingTripId
                )

                val result = alertRepository.createAlert(alert)
                if (result is Resource.Success) {
                    _createSuccess.value = true
                    loadAlerts()
                    _uiState.value = UIState(isLoading = false)
                } else {
                    _uiState.value = UIState(isLoading = false, message = result.message ?: "Error al crear alerta")
                }
            } else {
                _uiState.value = UIState(isLoading = false, message = ongoingResult.message ?: "No se encontro ongoing trip")
            }
        }
    }

    fun resetCreateSuccess() {
        _createSuccess.value = false
    }
}
