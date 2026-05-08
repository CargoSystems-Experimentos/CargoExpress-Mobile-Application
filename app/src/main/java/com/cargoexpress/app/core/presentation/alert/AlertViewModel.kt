package com.cargoexpress.app.core.presentation.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.domain.Alert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource
import pe.edu.upc.appturismo.common.UIState

class AlertViewModel(private val alertRepository: AlertRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UIState<List<Alert>>(isLoading = true))
    val uiState: StateFlow<UIState<List<Alert>>> = _uiState

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    init {
        loadAlerts()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            println("Loading alerts...")
            val result = alertRepository.getAlerts(Constants.TOKEN)
            if (result is Resource.Success) {
                println("Alerts loaded successfully: ${result.data}")
                _alerts.value = result.data ?: emptyList()
            } else {
                println("Failed to load alerts: ${result.message}")
                handleError(Exception(result.message))
            }
        }
    }

    fun getAlertById(tripId: Int): Alert? {
        val alert = _alerts.value.find { it.tripId == tripId }
        println("Getting alert by ID $tripId: $alert")
        return alert
    }

    private fun handleError(exception: Exception) {
        val message = exception.message ?: "Unknown error"
        println("Error occurred: $message")
        _uiState.value = UIState(isLoading = false, message = "Error: $message")
    }
}