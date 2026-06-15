package com.cargoexpress.app.core.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import com.cargoexpress.app.core.common.UIState
import com.cargoexpress.app.core.data.repository.AuditLogRepository
import com.cargoexpress.app.core.domain.AuditLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val auditLogRepository: AuditLogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState<List<AuditLog>>())
    val uiState: StateFlow<UIState<List<AuditLog>>> = _uiState

    fun loadLogs() {
        viewModelScope.launch {
            _uiState.value = UIState(isLoading = true)
            val result = auditLogRepository.getAuditLogsByEntrepreneur(
                Constants.TOKEN,
                Constants.ENTREPRENEUR_ID
            )
            _uiState.value = when (result) {
                is Resource.Success -> UIState(data = result.data ?: emptyList())
                is Resource.Error -> UIState(message = result.message ?: "Error al cargar historial")
            }
        }
    }
}
