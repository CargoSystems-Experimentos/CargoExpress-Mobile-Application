package com.cargoexpress.app.core.presentation.gps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.OngoingTripRepository

class GpsViewModelFactory(
    private val ongoingTripRepository: OngoingTripRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GpsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GpsViewModel(ongoingTripRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}