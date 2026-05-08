package com.cargoexpress.app.core.presentation.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.AlertRepository

class AlertViewModelFactory(
    private val alertRepository: AlertRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertViewModel(alertRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}