package com.cargoexpress.app.core.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.AuditLogRepository

class HistoryViewModelFactory(private val auditLogRepository: AuditLogRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HistoryViewModel(auditLogRepository) as T
    }
}
