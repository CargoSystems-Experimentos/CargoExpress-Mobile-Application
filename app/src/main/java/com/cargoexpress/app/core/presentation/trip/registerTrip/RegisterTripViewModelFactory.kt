package com.cargoexpress.app.core.presentation.trip.registerTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository

class RegisterTripViewModelFactory(
    private val tripRepository: TripRepository,
    private val entrepreneurRepository: EntrepreneurRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterTripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterTripViewModel(tripRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}