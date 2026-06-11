package com.cargoexpress.app.core.presentation.vehicle.registerVehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Vehicle
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource
import kotlin.String

class RegisterVehicleViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    var name: String = ""
    var model: String = ""
    var plate: String = ""
    var tractorPlate: String = ""
    var maxLoad: Double = 0.0
    var volume: Double = 0.0

    fun registerVehicle(onResult: (Resource<Vehicle>) -> Unit) {
        viewModelScope.launch {
            val vehicle = Vehicle(
                id = 0,
                name = name,
                model = model,
                plate = plate,
                tractorPlate = tractorPlate,
                maxLoad = maxLoad,
                volume = volume,
                state = "AVAILABLE",
                entrepreneurId = Constants.ENTREPRENEUR_ID
            )
            val result = vehicleRepository.addVehicle(vehicle)
            onResult(result)
        }
    }
}