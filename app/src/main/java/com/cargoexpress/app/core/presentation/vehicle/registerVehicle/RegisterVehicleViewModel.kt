package com.cargoexpress.app.core.presentation.vehicle.registerVehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.domain.Driver
import com.cargoexpress.app.core.domain.Vehicle
import kotlinx.coroutines.launch
import pe.edu.upc.appturismo.common.Constants
import pe.edu.upc.appturismo.common.Resource
import kotlin.String

class RegisterVehicleViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    var model: String = ""
    var plate: String = ""
    var tractorPlate: String = ""
    var maxLoad: Float = 0.0f
    var volume: Float = 0.0f

    fun registerVehicle(onResult: (Resource<Vehicle>) -> Unit) {
        viewModelScope.launch {
            val vehicle = Vehicle(
                id = 0,
                model = model,
                plate = plate,
                tractorPlate = tractorPlate,
                maxLoad = maxLoad,
                volume = volume,
                entrepreneurId = Constants.ENTREPRENEUR_ID
            )
            val result = vehicleRepository.addVehicle(vehicle)
            onResult(result)
        }
    }
}