package com.cargoexpress.app.core.presentation.trip


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.data.repository.TripRepository

class TripManagementViewModelFactory(
    private val tripRepository: TripRepository,
    private val ongoingTripRepository: OngoingTripRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripManagementViewModel(tripRepository, ongoingTripRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}